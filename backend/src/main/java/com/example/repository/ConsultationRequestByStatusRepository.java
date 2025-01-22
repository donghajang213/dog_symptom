package com.example.repository;

import com.example.entity.ConsultationRequest;
import com.example.entity.ConsultationRequestByStatus;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRequestByStatusRepository extends CassandraRepository<ConsultationRequestByStatus, UUID> {
//    @Query("SELECT * FROM consultation_requests_by_status WHERE status = :status AND request_id = :requestId")
//    ConsultationRequestByStatus findByStatusAndRequestId(@Param("status") String status, @Param("requestId") UUID requestId);


    // 모든 상태의 상담 요청을 조회하는 메서드 추가
//    @Query("SELECT * FROM consultation_requests_by_status WHERE request_id = :requestId ALLOW FILTERING") // ALLOW FILTERING 추가
//    Optional<ConsultationRequestByStatus> findByRequestId(@Param("requestId") UUID requestId);

    // request_id와 status를 함께 사용하여 쿼리
    @Query("SELECT * FROM consultation_requests_by_status WHERE request_id = :requestId AND status = :status ALLOW FILTERING")
    Optional<ConsultationRequestByStatus> findByRequestIdAndStatus(@Param("requestId") UUID requestId, @Param("status") String status);

    @Query("UPDATE consultation_requests_by_status SET status = :newStatus WHERE request_id = :requestId AND status = :oldStatus")
    void updateStatusByRequestIdAndStatus(@Param("requestId") UUID requestId, @Param("oldStatus") String oldStatus, @Param("newStatus") String newStatus);

    @Query("SELECT * FROM consultation_requests_by_status WHERE status = :status")
    List<ConsultationRequestByStatus> findByStatus(@Param("status") String status);

    Optional<ConsultationRequestByStatus> findByRequestId(UUID requestId);

    // Custom query to fetch by userId
    List<ConsultationRequestByStatus> findByUserId(String userId);

    // Custom query to fetch by vetId
    List<ConsultationRequestByStatus> findByVetId(UUID vetId);
}
