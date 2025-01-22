package com.example.repository.DashboardRepository;

import com.example.entity.DashboardEntity.VetRankingEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * 수의사 랭킹 Repository
 */
@Repository
public interface VetRankingRepository extends CassandraRepository<VetRankingEntity, Integer> {
}