package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("chat_room")
public class ChatRoom {

    @Id
    @PrimaryKey
    @Column("chat_id")
    private UUID chatId;

    @Column("request_id")
    private UUID requestId;

    @Column("user_id")
    private String userId;

    @Column("vet_id")
    private UUID vetId;

    @Column("created_at")
    private Instant createdAt;

    // 기본 생성자
    public ChatRoom() {}

    // 매개변수 생성자
    public ChatRoom(String userId, UUID vetId) {
        this.chatId = UUID.randomUUID(); // 새 UUID 생성
        this.userId = userId;
        this.vetId = vetId;
    }

}
