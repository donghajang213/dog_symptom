package com.example.repository.DashboardRepository;

import com.example.entity.PetEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BreedRankingRepository extends CassandraRepository<PetEntity, UUID> {

    // 품종별로 강아지 데이터를 가져오는 쿼리
    @Query("SELECT breed FROM pets")
    List<PetEntity> findAllBreeds();
}


