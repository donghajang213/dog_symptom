package com.example.repository;

import com.example.entity.ChatRoom;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository  extends CassandraRepository<ChatRoom, UUID> {
    // 특정 상담 요청 UUID, userId로 채팅방 찾기
    Optional<ChatRoom> findByUserIdAndVetId(String userId, UUID vetId);

    // requestId로 채팅방을 찾는 메서드
    Optional<ChatRoom> findByRequestId(UUID requestId);
}
