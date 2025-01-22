package com.example.service.DashboardService;

import com.example.entity.DashboardEntity.VetRankingEntity;
import com.example.repository.DashboardRepository.VetRankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 수의사 랭킹 서비스
 */
@Service
public class VetRankingService {

    private final VetRankingRepository vetRankingRepository;

    public VetRankingService(VetRankingRepository vetRankingRepository) {
        this.vetRankingRepository = vetRankingRepository;
    }

    public List<VetRankingEntity> getAllVetRankings() {
        return vetRankingRepository.findAll();
    }
}