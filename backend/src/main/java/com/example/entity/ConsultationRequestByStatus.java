package com.example.entity;

import com.example.service.ConsultationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("consultation_requests_by_status")
public class ConsultationRequestByStatus {

    @PrimaryKeyColumn(name = "status", type = PrimaryKeyType.PARTITIONED)
    private ConsultationStatus status;

    @PrimaryKeyColumn(name = "request_id", type = PrimaryKeyType.CLUSTERED)
    private UUID requestId;

    @Column("vet_id") // vetid가 아니라 vet_id로 매핑
    private UUID vetId;

    @Column("user_id")
    private String userId;

    @Column("predicted_disease")
    private String predictedDisease;

    @Column("processed_image_path")
    private String processedImagePath;

    @Column("created_at")
    private Instant createdAt;

    // Getters and Setters


    public ConsultationRequestByStatus(ConsultationStatus status, UUID requestId, UUID vetId, String userId, String predictedDisease, String processedImagePath, Instant createdAt) {
        this.status = status;
        this.requestId = requestId;
        this.vetId = vetId;
        this.userId = userId;
        this.predictedDisease = predictedDisease;
        this.processedImagePath = processedImagePath;
        this.createdAt = createdAt;
    }
}

