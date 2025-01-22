package com.example.controller;

import com.example.entity.ChatMessage;
import com.example.entity.ChatRoom;
import com.example.service.ChatService;
import com.example.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final ConsultationService consultationService;

    @Autowired
    public ChatController(ChatService chatService, ConsultationService consultationService) {
        this.chatService = chatService;
        this.consultationService = consultationService;
    }

    // 채팅방 정보 조회 (requestId로 채팅방을 찾아서 반환)
    @GetMapping("/rooms")
    public ResponseEntity<?> getChatRoom(@RequestParam UUID requestId) {
        // requestId로 상담 요청을 먼저 찾고, 해당 요청이 있으면 채팅방을 생성
        var consultationRequest = consultationService.getConsultationRequestByRequestId(requestId);
        if (consultationRequest.isPresent()) {
            // 상담 요청이 존재하면 채팅방을 찾거나 새로 생성
            ChatRoom chatRoom = chatService.findChatRoomByRequestId(requestId);
            if (chatRoom == null) {
                // 채팅방이 없다면 새로 생성
                chatRoom = chatService.createChatRoom(requestId, consultationRequest.get().getUserId(), consultationRequest.get().getVetId());
            }
            return ResponseEntity.ok(Map.of("chatId", chatRoom.getChatId()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consultation request not found");
        }
    }

    // 채팅방 메시지 조회
    @GetMapping("/rooms/{chatId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable UUID chatId) {
        List<ChatMessage> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // 채팅방에 메시지 보내기
    @PostMapping("/rooms/{chatId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable UUID chatId,
                                                   @RequestParam String senderId,
                                                   @RequestParam String message) {
        // 서비스 메서드를 호출하여 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(chatId, senderId, message);

        // 저장된 메시지를 응답으로 반환
        return ResponseEntity.ok(savedMessage);
    }
}
