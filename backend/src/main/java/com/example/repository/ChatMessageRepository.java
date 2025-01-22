package com.example.repository;

import com.datastax.oss.protocol.internal.Message;
import com.example.entity.ChatMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, UUID> {
    // 특정 채팅방의 모든 메시지 조회
    List<ChatMessage> findByChatId(UUID chatId);

    List<Message> findByChatIdOrderBySentAtAsc(UUID chatId);
}
