//package com.example.repository;
//
//import com.example.entity.ChatEntity;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ChatRepository extends CassandraRepository<ChatEntity, String> {
//    List<ChatEntity> findByRoomIdOrderByChatTimeAsc(String roomId);  // roomId로 메시지 순차적으로 조회
//}
//
