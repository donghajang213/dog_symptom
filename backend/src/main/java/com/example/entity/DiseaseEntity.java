package com.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("diseases") // Cassandra 테이블 이름
@Data
public class DiseaseEntity {

    @Id
    @Column("disease_id")
    private UUID diseaseId;          // 강아지질병ID

    @Column("origin_image_path")
    private String originImagePath; // 원본 이미지 경로

    @Column("processed_image_path")
    private String processedImagePath; // 분석된 이미지 경로

    @Column("upload_date")
    private LocalDate uploadDate;    // 업로드날짜 (LocalDate로 변경)

    @Column("predicted_disease_id")
    private int predictedDiseaseId;  // 모델이 예측한 질병 ID

    @Column("predicted_disease")
    private String predictedDisease; // 모델질병명

    @Column("status")
    private String status;           // 상태 (PENDING, COMPLETED, FAILED)

    @Column("user_id")
    private String userId;           // 사용자ID

    @Column("pet_id")
    private String dogId;            // 강아지ID
}