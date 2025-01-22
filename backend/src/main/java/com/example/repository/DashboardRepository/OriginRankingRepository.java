package com.example.repository.DashboardRepository;

import com.example.entity.DashboardEntity.OriginRankingEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * 제조사 랭킹 Repository
 */
@Repository
public interface OriginRankingRepository extends CassandraRepository<OriginRankingEntity, String> {
}