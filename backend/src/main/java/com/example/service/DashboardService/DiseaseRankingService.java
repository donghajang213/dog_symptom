package com.example.service.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.repository.DashboardRepository.DiseaseRankingRepository;
import com.example.entity.DiseaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiseaseRankingService {

    @Autowired
    private DiseaseRankingRepository diseaseRankingRepository;

    // 질병별 발생 빈도 조회
    public List<Map<String, Object>> getRankingByDisease() {
        // 모든 질병 데이터 조회
        List<DiseaseEntity> diseases = diseaseRankingRepository.findAllDiseases();

        // 질병 빈도 계산
        Map<String, Integer> diseaseCountMap = new HashMap<>();
        for (DiseaseEntity disease : diseases) {
            String diseaseName = disease.getPredictedDisease();
            diseaseCountMap.put(diseaseName, diseaseCountMap.getOrDefault(diseaseName, 0) + 1);
        }

        // 결과 데이터 포맷
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : diseaseCountMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("predictedDisease", entry.getKey());
            data.put("casesCount", entry.getValue());
            result.add(data);
        }

        return result;
    }

    // 가장 많이 발생한 질병 조회
    public Map<String, Object> getMostFrequentDisease() {
        List<Map<String, Object>> diseases = getRankingByDisease();

        // 가장 많이 발생한 질병 찾기
        String mostFrequentDisease = null;
        int maxCount = 0;
        for (Map<String, Object> disease : diseases) {
            int count = (int) disease.get("casesCount");
            if (count > maxCount) {
                mostFrequentDisease = (String) disease.get("predictedDisease");
                maxCount = count;
            }
        }

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("predictedDisease", mostFrequentDisease);
        result.put("casesCount", maxCount);
        return result;
    }
}




