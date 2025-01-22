package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("consultation_requests")
public class ConsultationRequest {

    @PrimaryKey
    @Column("request_id")
    private UUID requestId;

    @Column("created_at")
    private Instant createdAt;

    @Column("predicted_disease")
    private String predictedDisease;

    @Column("processed_image_path")
    private String processedImagePath;

    @Column("status")
    private String status;

    @Column("user_id")
    private String userId;

    @Column("vet_id")
    private UUID vetId;


}