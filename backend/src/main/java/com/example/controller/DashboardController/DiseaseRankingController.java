package com.example.controller.DashboardController;

import com.example.service.DashboardService.DiseaseRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/disease-ranking")
public class DiseaseRankingController {

    @Autowired
    private DiseaseRankingService diseaseRankingService;

    // 가장 많이 발생한 질병 조회
    @GetMapping("/most-frequent-disease")
    public ResponseEntity<Map<String, Object>> getMostFrequentDisease() {
        Map<String, Object> mostFrequentDisease = diseaseRankingService.getMostFrequentDisease();
        return ResponseEntity.ok(mostFrequentDisease);
    }

    // 질병별 발생 빈도 조회
    @GetMapping("/by-disease")
    public ResponseEntity<List<Map<String, Object>>> getRankingByDisease() {
        List<Map<String, Object>> ranking = diseaseRankingService.getRankingByDisease();
        return ResponseEntity.ok(ranking);
    }
}


