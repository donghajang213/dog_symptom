package com.example.service.DashboardService;

import com.example.entity.DashboardEntity.ProductRankingEntity;
import com.example.repository.DashboardRepository.ProductRankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 상품 랭킹 서비스
 */
@Service
public class ProductRankingService {

    private final ProductRankingRepository productRankingRepository;

    public ProductRankingService(ProductRankingRepository productRankingRepository) {
        this.productRankingRepository = productRankingRepository;
    }

    public List<ProductRankingEntity> getAllRankings() {
        return productRankingRepository.findAll();
    }
}