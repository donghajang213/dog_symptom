package com.example.repository;

import com.example.entity.ConsultationRequest;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface  ConsultationRequestRepository extends CassandraRepository<ConsultationRequest, UUID> {
    List<ConsultationRequest> findByVetId(UUID vetId);

    @Query("INSERT INTO consultation_requests (request_id, user_id, vet_id, predicted_disease, processed_image_path, status, created_at) VALUES (:requestId, :userId, :vetId, :predictedDisease, :processedImagePath, :status, :createdAt)")
    void insertConsultationRequest(
            @Param("requestId") UUID requestId,
            @Param("userId") String userId,
            @Param("vetId") UUID vetId,
            @Param("predictedDisease") String predictedDisease,
            @Param("processedImagePath") String processedImagePath,
            @Param("status") String status,
            @Param("createdAt") Instant createdAt
    );

    List<ConsultationRequest> findByVetIdAndStatus(UUID vetId, String status);

}