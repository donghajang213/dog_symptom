package com.example.service.DashboardService;

import com.example.entity.DashboardEntity.OriginRankingEntity;
import com.example.repository.DashboardRepository.OriginRankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 제조사 랭킹 서비스
 */
@Service
public class OriginRankingService {

    private final OriginRankingRepository originRankingRepository;

    public OriginRankingService(OriginRankingRepository originRankingRepository) {
        this.originRankingRepository = originRankingRepository;
    }

    public List<OriginRankingEntity> getAllRankings() {
        return originRankingRepository.findAll();
    }
}