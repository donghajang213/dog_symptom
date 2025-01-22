package com.example.controller.DashboardController;


import com.example.service.DashboardService.BreedRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pet-ranking")
public class BreedRankingController {

    @Autowired
    private BreedRankingService BreedRankingService;

    // 품종별 강아지 수 조회
    @GetMapping("/by-breed")
    public ResponseEntity<List<Map<String, Object>>> getPetCountByBreed() {
        List<Map<String, Object>> petCountByBreed = BreedRankingService.getPetCountByBreed();
        return ResponseEntity.ok(petCountByBreed);
    }
}



