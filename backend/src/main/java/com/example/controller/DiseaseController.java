package com.example.controller;

import com.example.entity.DiseaseEntity;
import com.example.service.DiseaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api") // "/api"를 기본 경로로 설정
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeImage(@RequestParam("file") MultipartFile file,
                                          @RequestParam("petId") String petId, // 프론트엔드에서 전달된 petId를 추가
                                          HttpSession session) {

        // 로그로 petId 확인
        System.out.println("전달받은 Pet ID: " + petId);

        // 세션에서 userId 가져오기
        String userId = (String) session.getAttribute("userId");

        // userId가 세션에 없으면 로그인되지 않은 상태
        if (userId == null) {
            System.out.println("로그인된 사용자 ID가 없습니다. 세션에 userId가 존재하지 않음.");
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        }

        try {
            // 서비스 호출 및 처리
            DiseaseEntity result = diseaseService.saveDiseaseData(file, petId, userId);
            // 성공적인 결과 반환
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 예외 발생 시 로깅 및 오류 메시지 반환
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }
}
