package com.example.service;

import com.example.entity.ChatMessage;
import com.example.entity.ChatRoom;
import com.example.repository.ChatMessageRepository;
import com.example.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public void createChatRoom(UUID requestId, String userId, UUID vetId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatId(UUID.randomUUID()); // 고유 ID 생성
        chatRoom.setRequestId(requestId);
        chatRoom.setUserId(userId);
        chatRoom.setVetId(vetId);
        chatRoom.setCreatedAt(Instant.now());

        chatRoomRepository.save(chatRoom); // 카산드라에 저장
    }

    // 메시지 추가
    @Transactional
    public void addMessage(UUID chatId, String senderId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(UUID.randomUUID());
        chatMessage.setChatId(chatId);
        chatMessage.setSenderId(senderId);
        chatMessage.setMessage(message);
        chatMessage.setSentAt(Instant.now());
        chatMessage.setRead(false);

        chatMessageRepository.save(chatMessage);
    }

    // 특정 채팅방의 메시지 조회
    public List<ChatMessage> getMessages(UUID chatId) {
        return chatMessageRepository.findByChatId(chatId);
    }
}
