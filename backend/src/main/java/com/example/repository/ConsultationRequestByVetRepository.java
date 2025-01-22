package com.example.repository;

import com.example.entity.ConsultationRequest;
import com.example.entity.ConsultationRequestByVet;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultationRequestByVetRepository extends CassandraRepository<ConsultationRequestByVet, UUID> {
    @Query("SELECT * FROM consultation_requests_by_vet WHERE vet_id = :vetId AND status = :status")
    List<ConsultationRequestByVet> findByVetIdAndStatus(@Param("vetId") UUID vetId, @Param("status") String status);

    @Query("SELECT * FROM consultation_requests_by_vet WHERE vet_id = :vetId")
    List<ConsultationRequestByVet> findByVetId(@Param("vetId") UUID vetId);
}

