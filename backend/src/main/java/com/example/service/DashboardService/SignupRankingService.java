package com.example.service.DashboardService;

import com.example.entity.UserEntity;
import com.example.repository.DashboardRepository.SignupRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SignupRankingService {

    @Autowired
    private SignupRankingRepository signupRankingRepository;

    // 월별 유저 가입 수 계산
    public List<Map<String, Object>> getUserCountByMonth() {
        // 모든 유저 데이터 조회
        List<UserEntity> users = signupRankingRepository.findAll();

        // 월별 유저 수를 계산
        Map<Integer, Integer> monthCountMap = new HashMap<>();
        for (UserEntity user : users) {
            if (user.getUserRegdate() != null) {
                int month = user.getUserRegdate().getMonthValue();  // 가입 월 추출

                // 월 값이 1에서 12 사이인 경우만 처리
                if (month >= 1 && month <= 12) {
                    monthCountMap.put(month, monthCountMap.getOrDefault(month, 0) + 1);
                }
            }
        }

        // 결과 데이터를 리스트 형태로 반환
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : monthCountMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", entry.getKey());
            data.put("userCount", entry.getValue());
            result.add(data);
        }

        return result;
    }
}
