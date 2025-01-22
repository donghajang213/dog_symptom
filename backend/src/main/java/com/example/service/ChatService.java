package com.example.service;


import com.datastax.oss.protocol.internal.Message;
import com.example.entity.ChatMessage;
import com.example.entity.ChatRoom;
import com.example.repository.ChatMessageRepository;

import com.example.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatRoom getOrCreateChatRoom(String userId, UUID vetId) {
        return chatRoomRepository.findByUserIdAndVetId(userId, vetId)
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(userId, vetId)));
    }

    public List<ChatMessage> getMessages(UUID chatId) {
        // chatId에 해당하는 메시지 조회
        return chatMessageRepository.findByChatId(chatId);
    }

    public ChatMessage saveMessage(UUID chatId, String senderId, String message) {
        ChatMessage newMessage = new ChatMessage();
        newMessage.setMessageId(UUID.randomUUID()); // UUID 생성
        newMessage.setChatId(chatId);
        newMessage.setSenderId(senderId);
        newMessage.setMessage(message);
        newMessage.setSentAt(Instant.now()); // Instant 사용
        newMessage.setRead(false); // 기본값은 읽지 않음으로 설정
        return chatMessageRepository.save(newMessage); // 반환 타입과 일치
    }

    // requestId로 채팅방을 찾는 메서드
    public ChatRoom findChatRoomByRequestId(UUID requestId) {
        return chatRoomRepository.findByRequestId(requestId).orElse(null);
    }

    // 채팅방 생성 메서드
    public ChatRoom createChatRoom(UUID requestId, String userId, UUID vetId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRequestId(requestId);
        chatRoom.setUserId(userId);
        chatRoom.setVetId(vetId);
        // 채팅방을 저장
        return chatRoomRepository.save(chatRoom);
    }

//    // 채팅방 메시지 조회
//    public List<ChatMessage> getMessages(UUID chatId) {
//        // 메시지 조회 로직 (필요 시 구현)
//    }
//
//    // 메시지 저장 및 반환 메서드
//    public ChatMessage saveMessage(UUID chatId, String senderId, String message) {
//        // 메시지 저장 로직 (필요 시 구현)
//    }

}
