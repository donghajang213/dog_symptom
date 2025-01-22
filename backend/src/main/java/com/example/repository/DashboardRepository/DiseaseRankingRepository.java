package com.example.repository.DashboardRepository;

import com.example.entity.DiseaseEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface DiseaseRankingRepository extends CassandraRepository<DiseaseEntity, UUID> {

    // 질병별로 발생 빈도 계산 (Cassandra에서 집계 함수 사용 제한)
    @Query("SELECT predicted_disease FROM diseases")
    List<DiseaseEntity> findAllDiseases();  // 모든 질병 데이터를 가져옴
}



