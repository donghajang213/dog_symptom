package com.example.controller;

import com.example.entity.CommentEntity;
import com.example.service.BoardService;
import com.example.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private BoardService boardService;

    @Autowired
    private HttpSession session;

    // 댓글 등록
    @PostMapping("/create")
    public ResponseEntity<String> createComment(
            @RequestParam("boardId") UUID boardId,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            String userId = (String) session.getAttribute("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("로그인이 필요합니다.");
            }

            CommentEntity comment = new CommentEntity();
            comment.setCommentId(UUID.randomUUID());
            comment.setBoardId(boardId);
            comment.setCommentContent(content);
            comment.setUserId(userId);
            comment.setCommentTime(LocalDateTime.now());

            if (image != null && !image.isEmpty()) {
                // 이미지 저장
                String savedPath = commentService.saveImage(image);
                comment.setCommentImagePath(savedPath);
                comment.setOriginCommentImagePath(image.getOriginalFilename());
            }

            commentService.saveComment(comment);
            return ResponseEntity.ok("댓글 작성 성공");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("댓글 작성 중 오류 발생: " + e.getMessage());
        }
    }

    // 특정 게시글의 댓글 조회
    @GetMapping("/list/{boardId}")
    public ResponseEntity<List<CommentEntity>> getCommentsByBoardId(@PathVariable UUID boardId) {
        List<CommentEntity> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/delete/{boardId}/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable UUID boardId,
            @PathVariable UUID commentId,
            HttpSession session) {
        try {
            // 사용자 인증
            String userId = (String) session.getAttribute("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 댓글 삭제
            commentService.deleteCommentByBoardIdAndCommentId(boardId, commentId);

            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
