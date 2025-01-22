//package com.example.entity;
//
//import lombok.Data;
//import org.springframework.data.cassandra.core.mapping.Column;
//import org.springframework.data.cassandra.core.mapping.PrimaryKey;
//import org.springframework.data.cassandra.core.mapping.Table;
//
//@Data
//@Table("chat")
//public class ChatEntity {
//
//    @PrimaryKey
//    @Column("chat_id")
//    private String chatId;  // 메시지 ID (고유)
//
//    @Column("room_id")
//    private String roomId;  // 대화방 ID
//
//    @Column("chat_input")
//    private String chatInput;  // 사용자의 입력 메시지
//
//    @Column("chat_output")
//    private String chatOutput;  // 수의사의 응답 메시지
//
//    @Column("user_id")
//    private String userId;  // 사용자 ID
//
//    @Column("vet_id")
//    private String vetId;  // 수의사 ID
//
//    @Column("chat_time")
//    private String chatTime;  // 채팅 시간
//
//}