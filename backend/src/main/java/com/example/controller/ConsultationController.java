package com.example.controller;

import com.example.entity.ConsultationRequest;
import com.example.entity.ConsultationRequestByStatus;
import com.example.entity.ConsultationRequestByVet;
import com.example.entity.VetEntity;
import com.example.repository.VetRepository;
import com.example.service.ConsultationService;
import com.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @PostMapping("/{vetId}/request")
    public ResponseEntity<?> requestConsultation(
            @PathVariable(name = "vetId") String vetId,
            HttpSession session,
            @RequestBody Map<String, String> request
    ) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            UUID vetUuid = UUID.fromString(vetId);  // String을 UUID로 변환
            consultationService.saveConsultationRequest(userId, vetUuid, request);
            return ResponseEntity.ok("상담 요청이 성공적으로 접수되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 vetId입니다.");
        }
    }

    @GetMapping("/{vetId}/requests")
    public ResponseEntity<List<ConsultationRequestByVet>> getConsultationRequests(
            @PathVariable(name = "vetId") String vetId
    ) {
        try {
            UUID vetUuid = UUID.fromString(vetId);  // String을 UUID로 변환
            List<ConsultationRequestByVet> requests = consultationService.getConsultationRequestsByVet(vetUuid);
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Vet ID: " + vetId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }




    /**
     * 상담 요청 목록과 세부 정보 가져오기
     */
    @GetMapping("/{vetId}/detailed-requests")
    public ResponseEntity<List<Map<String, Object>>> getConsultationRequestsWithDetails(@PathVariable String vetId) {
        try {
            UUID vetUuid = UUID.fromString(vetId);
            return ResponseEntity.ok(consultationService.getConsultationRequestsWithDetails(vetUuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 미확인 상담 요청 개수
     */
    @GetMapping("/{vetId}/pendingRequestCount")
    public ResponseEntity<Integer> getPendingConsultationRequestCount(@PathVariable String vetId) {
        try {
            UUID vetUuid = UUID.fromString(vetId);
            return ResponseEntity.ok(consultationService.getPendingConsultationRequestCount(vetUuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(0);
        }
    }

    /**
     * 상담 요청 수락
     */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptConsultationRequest(@PathVariable String requestId) {
        try {
            UUID requestUuid = UUID.fromString(requestId);
            consultationService.acceptConsultationRequest(requestUuid);

            // 클라이언트 웹 소캣 공지
            messagingTemplate.convertAndSend("/topic/consultation", Map.of(
                    "type", "ACCEPT",
                    "requestId", requestId,
                    "message", "상담 요청 수락"
            ));

            return ResponseEntity.ok("상담 요청을 수락했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 Request ID: " + requestId);
        }
    }

    /**
     * 상담 요청 거절
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<String> rejectConsultationRequest(@PathVariable String requestId) {
        try {
            UUID requestUuid = UUID.fromString(requestId);
            consultationService.rejectConsultationRequest(requestUuid);

            // 클라이언트 웹 소캣 공지
            messagingTemplate.convertAndSend("/topic/consultation", Map.of(
                    "type", "REJECT",
                    "requestId", requestId,
                    "message", "상담 요청 거절"
            ));
            return ResponseEntity.ok("상담 요청을 거절했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 Request ID");
        }
    }

    @GetMapping("/get-user-id")
    public ResponseEntity<?> getUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @GetMapping("/get-vet-id")
    public ResponseEntity<?> getVetId(@RequestParam("userId") String userId) {
        // Validate the input
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId is required.");
        }

        try {
            // Fetch the vet information by userId
            Optional<VetEntity> vetOptional = vetRepository.findByUserId(userId);
            if (vetOptional.isPresent()) {
                VetEntity vet = vetOptional.get();
                UUID vetId = vet.getVetId();
                return ResponseEntity.ok(Map.of("vetId", vetId));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vet not found for the given userId.");
            }
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{requestId}/cancel")
    public ResponseEntity<?> cancelConsultationRequest(@PathVariable String requestId) {
        try {
            UUID requestUuid = convertToUUID(requestId);
            consultationService.cancelConsultationRequest(requestUuid);

            // WebSocket 메시지 전송
            consultationService.sendWebSocketMessage("/topic/consultation", "CANCEL", requestId, "상담 요청 취소");

            return ResponseEntity.ok("상담 요청이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 Request ID");
        }
    }

    private UUID convertToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 UUID 형식입니다: " + id);
        }
    }


}