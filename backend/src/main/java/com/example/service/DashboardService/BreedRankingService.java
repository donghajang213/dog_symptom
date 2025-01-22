package com.example.service.DashboardService;

import com.example.entity.PetEntity;
import com.example.repository.DashboardRepository.BreedRankingRepository;
import com.example.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BreedRankingService {

    @Autowired
    private BreedRankingRepository BreedRankingRepository;

    // 품종별 강아지 수 계산
    public List<Map<String, Object>> getPetCountByBreed() {
        // 모든 강아지 데이터를 가져옴
        List<PetEntity> pets = BreedRankingRepository.findAllBreeds();

        // 품종별 강아지 수 계산
        Map<String, Integer> breedCountMap = new HashMap<>();
        for (PetEntity pet : pets) {
            String breed = pet.getBreed();
            breedCountMap.put(breed, breedCountMap.getOrDefault(breed, 0) + 1);
        }

        // 결과 데이터를 반환할 리스트로 포맷
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : breedCountMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("breed", entry.getKey());
            data.put("count", entry.getValue());
            result.add(data);
        }

        return result;
    }
}