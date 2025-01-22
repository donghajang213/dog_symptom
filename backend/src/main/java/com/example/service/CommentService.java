package com.example.service;

import com.example.entity.CommentEntity;
import com.example.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    private final String uploadDir = "uploads/comments"; // 이미지 저장 경로

    // 댓글 저장
    public void saveComment(CommentEntity comment) {
        commentRepository.save(comment);
    }

    // 특정 게시글의 댓글 조회
    public List<CommentEntity> getCommentsByBoardId(UUID boardId) {
        return commentRepository.findAllByBoardId(boardId);
    }

    // 이미지 저장
    public String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);

        // 디렉토리 생성
        Files.createDirectories(path.getParent());

        // 파일 저장
        Files.copy(file.getInputStream(), path);

        return path.toString();
    }

    // 특정 댓글 삭제 (board_id + comment_id)
    public void deleteCommentByBoardIdAndCommentId(UUID boardId, UUID commentId) {
        commentRepository.deleteByBoardIdAndCommentId(boardId, commentId);
    }

    // 특정 게시글의 모든 댓글 삭제
    public void deleteCommentsByBoardId(UUID boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
