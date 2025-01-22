package com.example.controller.DashboardController;

import com.example.service.DashboardService.SignupRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-ranking")
public class SignupRankingController {

    @Autowired
    private SignupRankingService signupRankingService;

    // 월별 유저 가입 수 조회
    @GetMapping("/by-month")
    public ResponseEntity<List<Map<String, Object>>> getUserCountByMonth() {
        List<Map<String, Object>> userCountByMonth = signupRankingService.getUserCountByMonth();
        return ResponseEntity.ok(userCountByMonth);
    }
}