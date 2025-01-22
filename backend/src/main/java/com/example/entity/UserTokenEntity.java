package com.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("tokens")
public class UserTokenEntity {
    @PrimaryKey
    @Id
    @Column("user_id")
    private String userId; // 사용자 ID

    @Column("total_tokens")
    private int totalTokens; // 보유 토큰 개수

    @Column("updated_at")
    private LocalDateTime updatedAt; // 마지막 업데이트 시간
}