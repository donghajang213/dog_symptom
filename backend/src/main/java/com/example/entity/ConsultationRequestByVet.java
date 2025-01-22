package com.example.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("consultation_requests_by_vet")
public class ConsultationRequestByVet {

    @PrimaryKeyColumn(name = "vet_id", type = PrimaryKeyType.PARTITIONED)
    private UUID vetId;

    @PrimaryKeyColumn(name = "status", type = PrimaryKeyType.CLUSTERED)
    private String status;

    @PrimaryKeyColumn(name = "request_id", type = PrimaryKeyType.CLUSTERED)
    private UUID requestId;

    @Column("user_id")
    private String userId;

    @Column("predicted_disease")
    private String predictedDisease;

    @Column("processed_image_path")
    private String processedImagePath;

    @Column("created_at")
    private Instant createdAt;

    public ConsultationRequestByVet(UUID vetId, String status, UUID requestId, String userId, String predictedDisease, String processedImagePath, Instant createdAt) {
        this.vetId = vetId;
        this.status = status;
        this.requestId = requestId;
        this.userId = userId;
        this.predictedDisease = predictedDisease;
        this.processedImagePath = processedImagePath;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getVetId() {
        return vetId;
    }

    public void setVetId(UUID vetId) {
        this.vetId = vetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPredictedDisease() {
        return predictedDisease;
    }

    public void setPredictedDisease(String predictedDisease) {
        this.predictedDisease = predictedDisease;
    }

    public String getProcessedImagePath() {
        return processedImagePath;
    }

    public void setProcessedImagePath(String processedImagePath) {
        this.processedImagePath = processedImagePath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
