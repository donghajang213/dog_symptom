package com.example.service;

import com.example.entity.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRequestByVetRepository consultationRequestByVetRepository;

    @Autowired
    private ConsultationRequestByStatusRepository consultationRequestByStatusRepository;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    public ConsultationService(ConsultationRequestByStatusRepository consultationRequestByStatusRepository, VetRepository vetRepository) {
        this.consultationRequestByStatusRepository = consultationRequestByStatusRepository;
        this.vetRepository = vetRepository;
    }

    public ConsultationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void sendWebSocketMessage(String destination, String type, String requestId, String message) {
        messagingTemplate.convertAndSend(destination, Map.of(
                "type", type,
                "requestId", requestId,
                "message", message
        ));
    }

    @Transactional
    public void saveConsultationRequest(String userId, UUID vetId, Map<String, String> request) {
        UUID requestId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        // Get disease details for the user
        List<DiseaseEntity> diseases = diseaseRepository.findByUserId(userId);
        if (diseases.isEmpty()) {
            throw new IllegalArgumentException("질병 데이터를 찾을 수 없습니다.");
        }
        DiseaseEntity disease = diseases.get(0);

        // Save to both tables
        ConsultationRequestByVet requestByVet = new ConsultationRequestByVet(
                vetId, ConsultationStatus.PENDING.name(), requestId, userId, disease.getPredictedDisease(),
                disease.getProcessedImagePath(), createdAt
        );

        ConsultationRequestByStatus requestByStatus = new ConsultationRequestByStatus(
                ConsultationStatus.PENDING, requestId, vetId, userId, disease.getPredictedDisease(),
                disease.getProcessedImagePath(), createdAt
        );

        consultationRequestByVetRepository.save(requestByVet);
        consultationRequestByStatusRepository.save(requestByStatus);

        // Send email to vet
        VetEntity vetEntity = vetRepository.findById(vetId)
                .orElseThrow(() -> new IllegalArgumentException("수의사를 찾을 수 없습니다. Vet ID: " + vetId));
        emailService.sendSimpleMessage(vetEntity.getEmail(), "새 상담 요청", "새로운 상담 요청이 접수되었습니다.");

        // Update consultation count
        updateVetConsultationCount(vetId);
    }

    private void updateVetConsultationCount(UUID vetId) {
        VetEntity vetEntity = vetRepository.findById(vetId)
                .orElseThrow(() -> new IllegalArgumentException("수의사를 찾을 수 없습니다. Vet ID: " + vetId));

        vetEntity.setConsultationCount(vetEntity.getConsultationCount() + 1);
        vetRepository.save(vetEntity);
    }

    public List<Map<String, Object>> getConsultationRequestsWithDetails(UUID vetId) {
        List<ConsultationRequestByVet> requests = consultationRequestByVetRepository.findByVetId(vetId);

        return requests.stream()
                .map(request -> Map.<String, Object>of(
                        "request_id", request.getRequestId().toString(),
                        "user_id", (Object) request.getUserId(), // 명시적 캐스팅
                        "status", (Object) request.getStatus(),
                        "created_at", (Object) request.getCreatedAt(),
                        "predicted_disease", (Object) request.getPredictedDisease(),
                        "processed_image_path", (Object) request.getProcessedImagePath()
                ))
                .collect(Collectors.toList());
    }

    public long countPendingConsultationsByVetId(UUID vetId) {
        System.out.println("Fetching pending consultations for vetId: " + vetId);
        long count = consultationRequestByStatusRepository.countPendingConsultationsByVetId(vetId);
        System.out.println("Pending consultations count: " + count);
        return count;
    }

    // userId로 vetId를 찾는 메소드 추가
    public UUID findVetIdByUserId(String userId) {
        return vetRepository.findVetIdByUserId(userId);
    }

    @Transactional
    public void acceptConsultationRequest(UUID requestId) {
        // request_id와 status를 함께 사용하여 쿼리
        Optional<ConsultationRequestByStatus> requestOptional = consultationRequestByStatusRepository.findByRequestIdAndStatus(requestId, ConsultationStatus.PENDING.name());
        ConsultationRequestByStatus request = requestOptional
                .orElseThrow(() -> new RuntimeException("Consultation request not found with id: " + requestId));

        // 이미 처리된 요청인지 확인
        if (request.getStatus() == ConsultationStatus.ACCEPTED || request.getStatus() == ConsultationStatus.REJECTED) {
            throw new IllegalStateException("Consultation request is already processed.");
        }

        // PENDING 상태인 경우에만 수락 처리
        if (request.getStatus() == ConsultationStatus.PENDING) {
            // 새로운 레코드 추가
            ConsultationRequestByStatus newRequest = new ConsultationRequestByStatus(
                    ConsultationStatus.ACCEPTED, requestId, request.getVetId(), request.getUserId(),
                    request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
            );
            consultationRequestByStatusRepository.save(newRequest);

            // 기존 레코드 삭제
            consultationRequestByStatusRepository.delete(request);

            ConsultationRequestByVet requestByVet = new ConsultationRequestByVet(
                    request.getVetId(), ConsultationStatus.ACCEPTED.name(), requestId, request.getUserId(),
                    request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
            );
            consultationRequestByVetRepository.save(requestByVet);

            chatRoomService.createChatRoom(requestId, request.getUserId(), request.getVetId());

            // WebSocket 메시지 전송
            sendWebSocketMessage("/topic/updates", "status_change", requestId.toString(), "Request Accepted");
        }
    }

    @Transactional
    public void rejectConsultationRequest(UUID requestId) {
        // findByRequestIdAndStatus 대신 findByRequestId 사용
        Optional<ConsultationRequestByStatus> requestOptional = consultationRequestByStatusRepository.findByRequestIdAndStatus(requestId, ConsultationStatus.PENDING.name());
        ConsultationRequestByStatus request = requestOptional
                .orElseThrow(() -> new RuntimeException("Consultation request not found with id: " + requestId));

        // 이미 처리된 요청인지 확인
        if (request.getStatus() == ConsultationStatus.ACCEPTED || request.getStatus() == ConsultationStatus.REJECTED) {
            throw new IllegalStateException("Consultation request is already processed.");
        }

        // PENDING 상태인 경우에만 거절 처리
        if (request.getStatus() == ConsultationStatus.PENDING) {
            // 새로운 레코드 추가
            ConsultationRequestByStatus newRequest = new ConsultationRequestByStatus(
                    ConsultationStatus.REJECTED, requestId, request.getVetId(), request.getUserId(),
                    request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
            );
            consultationRequestByStatusRepository.save(newRequest);

            // 기존 레코드 삭제
            consultationRequestByStatusRepository.delete(request);

            ConsultationRequestByVet requestByVet = new ConsultationRequestByVet(
                    request.getVetId(), ConsultationStatus.REJECTED.name(), requestId, request.getUserId(),
                    request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
            );
            consultationRequestByVetRepository.save(requestByVet);

            messagingTemplate.convertAndSend("/topic/updates", Map.of(
                    "requestId", requestId,
                    "status", ConsultationStatus.REJECTED.name() // enum 값을 문자열로 변환하여 설정
            ));
        }
    }

    @Transactional
    public void cancelConsultationRequest(UUID requestId) {
        // findByRequestIdAndStatus 대신 findByRequestId 사용
        Optional<ConsultationRequestByStatus> requestOptional = consultationRequestByStatusRepository.findByRequestIdAndStatus(requestId, ConsultationStatus.PENDING.name());
        ConsultationRequestByStatus request = requestOptional
                .orElseThrow(() -> new RuntimeException("Consultation request not found with id: " + requestId));

        // PENDING 상태의 요청만 취소 가능
        if (request.getStatus() != ConsultationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be cancelled.");
        }

        // 새로운 레코드 추가
        ConsultationRequestByStatus newRequest = new ConsultationRequestByStatus(
                ConsultationStatus.CANCELLED, requestId, request.getVetId(), request.getUserId(),
                request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
        );
        consultationRequestByStatusRepository.save(newRequest);

        // 기존 레코드 삭제
        consultationRequestByStatusRepository.delete(request);

        ConsultationRequestByVet requestByVet = new ConsultationRequestByVet(
                request.getVetId(), ConsultationStatus.CANCELLED.name(), requestId, request.getUserId(),
                request.getPredictedDisease(), request.getProcessedImagePath(), request.getCreatedAt()
        );
        consultationRequestByVetRepository.save(requestByVet);
    }

    public List<ConsultationRequestByVet> getConsultationRequestsByVet(UUID vetId) {
        return consultationRequestByVetRepository.findByVetId(vetId);
    }

    public List<ConsultationRequestByStatus> getConsultationRequestsByStatus(String status) {
        return consultationRequestByStatusRepository.findByStatus(status);
    }

    public Optional<ConsultationRequestByStatus> getConsultationRequestByRequestId(UUID requestId) {
        return consultationRequestByStatusRepository.findByRequestId(requestId);
    }

    // 채팅방 생성 메서드
    public ChatRoom createChatRoom(UUID requestId, String userId, UUID vetId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRequestId(requestId);
        chatRoom.setUserId(userId);
        chatRoom.setVetId(vetId);
        // 채팅방을 저장
        return chatRoomRepository.save(chatRoom);
    }

//    // 메시지 저장 및 반환 메서드
//    public ChatMessage saveMessage(UUID chatId, String senderId, String message) {
//        // 메시지 저장 로직 (필요 시 구현)
//    }
//
//    // 채팅방 메시지 조회
//    public List<ChatMessage> getMessages(UUID chatId) {
//        // 메시지 조회 로직 (필요 시 구현)
//    }
}

