package com.example.service;


import com.example.entity.ConsultationRequest;
import com.example.entity.VetEntity;
//import com.example.entity.VetInfoRedis;
import com.example.repository.ConsultationRequestRepository;
//import com.example.repository.VetInfoRedisRepository;
import com.example.repository.VetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class VetService {

    @Autowired
    private VetRepository vetRepository;

//    @Autowired
//    private VetInfoRedisRepository vetInfoRedisRepository;

    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;

    @Transactional
    public void incrementConsultationCount(UUID vetId) {
        // 업데이트 cassandra
        VetEntity vetInfo = vetRepository.findById(vetId).orElseThrow(() -> new RuntimeException("Vet not found"));
        vetInfo.setConsultationCount(vetInfo.getConsultationCount() + 1);
        vetRepository.save(vetInfo);

//        // 업데이트 redis
//        VetInfoRedis vetInfoRedis = vetInfoRedisRepository.findById(vetId).orElseThrow(() -> new RuntimeException("Vet not found"));
//        vetInfoRedis.setConsultationCount(vetInfoRedis.getConsultationCount() + 1);
//        vetInfoRedisRepository.save(vetInfoRedis);
    }
    @Transactional public void incrementConsultationCountAndSaveRequest(UUID vetId, String userId, String predictedDisease, String processedImagePath) {
        // 업데이트 cassandra 및 redis
        incrementConsultationCount(vetId);
        // 상담 요청 정보 저장
        ConsultationRequest consultationRequest = new ConsultationRequest();
        consultationRequest.setRequestId(UUID.randomUUID());
        consultationRequest.setVetId(vetId);
        consultationRequest.setUserId(userId);
        consultationRequest.setPredictedDisease(predictedDisease);
        consultationRequest.setProcessedImagePath(processedImagePath);
        consultationRequest.setStatus("PENDING");
        consultationRequest.setCreatedAt(Instant.now());

        consultationRequestRepository.save(consultationRequest);
    }




    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (킬로미터)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 결과 거리 (킬로미터)
    }

    public List<VetEntity> getAllVets() {
        return vetRepository.findAll();
    }

    public Optional<VetEntity> getVetById(UUID vetId) {
        return vetRepository.findById(vetId);
    }

    public VetEntity saveVet(VetEntity vet) {
        return vetRepository.save(vet);
    }

    public void deleteVet(UUID vetId) {
        vetRepository.deleteById(vetId);
    }

    // 모든 수의사를 불러오고 거리순으로 정렬하는 메서드
    public List<VetEntity> getVetsSortedByDistance(double userLat, double userLon) {
        List<VetEntity> allVets = vetRepository.findAll();
        return allVets.stream()
                .map(vet -> {
                    double distance = calculateDistance(userLat, userLon, vet.getLatitude(), vet.getLongitude());
                    vet.setDistance(distance);
                    return vet;
                })
                .sorted(Comparator.comparingDouble(VetEntity::getDistance))
                .collect(Collectors.toList());
    }

    public List<VetEntity> getVetsSortedByCriteria(String criteria) {
        List<VetEntity> allVets = vetRepository.findAll();
        return allVets.stream()
                .sorted((a, b) -> {
                    switch (criteria) {
                        case "distance":
                            return Double.compare(a.getDistance(), b.getDistance());
                        case "rating":
                            return Float.compare(b.getVetRating(), a.getVetRating());
                        case "reviews":
                            return Integer.compare(b.getReviewCount(), a.getReviewCount());
                        case "consultations":
                            return Integer.compare(b.getConsultationCount(), a.getConsultationCount());
                        default:
                            return 0;
                    }
                })
                .collect(Collectors.toList());
    }
}