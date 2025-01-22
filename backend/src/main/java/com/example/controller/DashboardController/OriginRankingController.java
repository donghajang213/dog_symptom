package com.example.controller.DashboardController;

import com.example.entity.DashboardEntity.OriginRankingEntity;
import com.example.service.DashboardService.OriginRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 제조사 랭킹 컨트롤러
 */
@RestController
@RequestMapping("/ranking/origin")
public class OriginRankingController {

    private final OriginRankingService originRankingService;

    public OriginRankingController(OriginRankingService originRankingService) {
        this.originRankingService = originRankingService;
    }

    @GetMapping
    public List<OriginRankingEntity> getOriginRankings() {
        return originRankingService.getAllRankings();
    }
}