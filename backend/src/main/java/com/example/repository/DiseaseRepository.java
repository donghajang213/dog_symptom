package com.example.repository;

import com.example.entity.DiseaseEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiseaseRepository extends CassandraRepository<DiseaseEntity, UUID> {
    // 사용자가 특정 질병 데이터를 가져오기 위한 쿼리 메서드
    @Query("SELECT * FROM diseases WHERE user_id = ?0 ALLOW FILTERING")
    List<DiseaseEntity> findByUserId(String userId);
}