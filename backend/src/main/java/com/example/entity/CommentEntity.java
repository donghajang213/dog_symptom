package com.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("comment")
public class CommentEntity {

    @PrimaryKey
    @Column("comment_id")
    private UUID commentId; // 댓글 ID
    @Column("comment_content")
    private String commentContent; // 댓글 내용
    @Column("comment_image_path")
    private String commentImagePath; // 댓글 이미지 경로
    @Column("origin_comment_image_path")
    private String originCommentImagePath; // 원본 이미지 파일명
    @Column("comment_time")
    private LocalDateTime commentTime; // 댓글 작성 시간
    @Column("user_id")
    private String userId; // 작성자 ID
    @Column("board_id")
    private UUID boardId; // 댓글이 속한 게시글 ID
}
