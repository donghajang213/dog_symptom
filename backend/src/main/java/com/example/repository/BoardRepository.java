package com.example.repository;

import com.example.entity.BoardEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends CassandraRepository<BoardEntity, UUID> {

    // 제목 또는 내용에 키워드 포함된 게시글 검색
    @Query("SELECT * FROM board WHERE board_title CONTAINS :keyword OR board_content CONTAINS :keyword ALLOW FILTERING")
    List<BoardEntity> searchByTitleOrContent(String keyword);
}
