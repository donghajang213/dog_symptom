package com.example.controller.DashboardController;

import com.example.entity.DashboardEntity.VetRankingEntity;
import com.example.service.DashboardService.VetRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수의사 랭킹 컨트롤러
 */
@RestController
@RequestMapping("/ranking/vet")
public class VetRankingController {

    private final VetRankingService vetRankingService;

    public VetRankingController(VetRankingService vetRankingService) {
        this.vetRankingService = vetRankingService;
    }

    @GetMapping
    public List<VetRankingEntity> getVetRankings() {
        return vetRankingService.getAllVetRankings();
    }
}