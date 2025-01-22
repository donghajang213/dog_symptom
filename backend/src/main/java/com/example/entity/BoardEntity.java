package com.example.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table("board")
@Data
public class BoardEntity {

    @PrimaryKey
    @Column("board_id")
    private UUID boardId; // 게시글 ID
    @Column("board_title")
    private String boardTitle; // 제목
    @Column("board_content")
    private String boardContent; // 내용
    @Column("board_view")
    private int boardView; // 조회수
    @Column("board_date")
    private LocalDateTime boardDate; // 작성일
    @Column("origin_image_path")
    private List<String> originImagePath;
    @Column("image_path")
    private List<String> imagePath;
    @Column("user_id")
    private String userId; // 작성자 ID
}
