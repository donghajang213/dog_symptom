package com.example.service;

import com.example.entity.DiseaseEntity;
import com.example.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public DiseaseEntity saveDiseaseData(MultipartFile file, String petId, String userId) {
        DiseaseEntity entity = new DiseaseEntity();

        try {
            // 디렉토리 생성
            File originalDir = new File(UPLOAD_DIR + "original/");
            File processedDir = new File(UPLOAD_DIR + "processed/");
            if (!originalDir.exists()) originalDir.mkdirs();
            if (!processedDir.exists()) processedDir.mkdirs();

            // 원본 이미지 저장
            String originalFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File originalFile = new File(originalDir, originalFileName);
            file.transferTo(originalFile);

            // 분석용 이미지 복사
            String processedFileName = "processed_" + originalFileName;
            File processedFile = new File(processedDir, processedFileName);
            Files.copy(originalFile.toPath(), processedFile.toPath());

            // 엔티티 데이터 생성
            entity.setDiseaseId(UUID.randomUUID());
            entity.setUserId(userId);
            entity.setDogId(petId); // 전달받은 petId 설정

            // LocalDate로 업로드 날짜 설정
            LocalDate uploadDate = LocalDate.now(ZoneId.systemDefault());
            entity.setUploadDate(uploadDate);

            // 이미지 경로 설정
            entity.setOriginImagePath("/uploads/original/" + originalFileName);
            entity.setProcessedImagePath("/uploads/processed/" + processedFileName);

            // 예측된 데이터 설정 (임의로 지정된 값)
            entity.setPredictedDiseaseId(1);
            entity.setPredictedDisease("구진, 플라크");
            entity.setStatus("COMPLETED");

            System.out.println("엔티티 데이터 생성 완료: " + entity);

            // 데이터 저장
            DiseaseEntity savedEntity = diseaseRepository.save(entity);
            System.out.println("데이터 저장 성공: " + savedEntity);

            return savedEntity;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }
}