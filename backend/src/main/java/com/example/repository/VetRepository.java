package com.example.repository;

import com.example.entity.VetEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VetRepository extends CassandraRepository<VetEntity, UUID> {

    List<VetEntity> findAll();

    Optional<VetEntity> findByUserId(String userId);

    // userId로 vetId 찾는 쿼리
    @Query("SELECT vet_id FROM vet_info WHERE user_id = ?0")
    UUID findVetIdByUserId(String userId);

}