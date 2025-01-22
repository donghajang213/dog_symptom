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
@Table("chat_message")
public class ChatMessage {
    @Id
    @PrimaryKey
    @Column("message_id")
    private UUID messageId;

    @Column("chat_id")
    private UUID chatId;

    @Column("sender_id")
    private String senderId;

    @Column("message")
    private String message;

    @Column("sent_at")
    private Instant sentAt;

    @Column("is_read")
    private boolean isRead;

}
