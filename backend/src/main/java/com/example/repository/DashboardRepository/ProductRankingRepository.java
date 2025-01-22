package com.example.repository.DashboardRepository;

import com.example.entity.DashboardEntity.ProductRankingEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * 상품 랭킹 Repository
 */
@Repository
public interface ProductRankingRepository extends CassandraRepository<ProductRankingEntity, Integer> {
}