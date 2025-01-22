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

}