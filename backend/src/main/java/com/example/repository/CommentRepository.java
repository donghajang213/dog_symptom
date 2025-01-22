package com.example.repository;

import com.example.entity.CommentEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends CassandraRepository<CommentEntity, UUID> {

    // 특정 게시글 ID로 댓글 조회
    @Query("SELECT * FROM comment WHERE board_id = ?0 ALLOW FILTERING")
    List<CommentEntity> findAllByBoardId(UUID boardId);

    // 특정 댓글 삭제
    void deleteById(UUID commentId);

    // 특정 게시글의 모든 댓글 삭제
    void deleteByBoardId(UUID boardId);

    @Query("DELETE FROM comment WHERE board_id = ?0 AND comment_id = ?1")
    void deleteByBoardIdAndCommentId(UUID boardId, UUID commentId);

}
