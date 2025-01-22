package com.example.controller;

import com.example.entity.BoardEntity;
import com.example.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private final BoardService boardService;

    @Autowired
    private HttpSession session; // 세션 사용

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 작성
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createBoard(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        Map<String, String> response = new HashMap<>();

        try {
            // 세션에서 userId 가져오기
            String userId = (String) session.getAttribute("userId");
            if (userId == null || userId.trim().isEmpty()) {
                response.put("message", "로그인되지 않았습니다. 사용자 ID를 세션에서 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 게시글 ID 생성
            UUID boardId = UUID.randomUUID();

            // BoardEntity 생성 및 값 설정
            BoardEntity board = new BoardEntity();
            board.setBoardId(boardId);
            board.setUserId(userId);
            board.setBoardTitle(title);
            board.setBoardContent(content);
            board.setBoardView(0); // 초기 조회수 0
            board.setBoardDate(LocalDateTime.now());

            // 이미지 파일 처리
            List<String> imagePaths = new ArrayList<>();
            List<String> originImagePaths = new ArrayList<>();
            if (photos != null && !photos.isEmpty()) {
                for (MultipartFile photo : photos) {
                    if (!photo.isEmpty()) {
                        String savedPath = saveFile(photo);
                        imagePaths.add(savedPath);
                        originImagePaths.add(photo.getOriginalFilename());
                    }
                }
            }
            board.setImagePath(imagePaths);
            board.setOriginImagePath(originImagePaths);

            // 서비스 호출하여 데이터 저장
            boardService.saveBoard(board);

            response.put("message", "게시글 작성 성공");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "파일 저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("message", "게시글 작성 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 게시글 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<BoardEntity>> getAllBoards() {
        try {
            List<BoardEntity> boards = boardService.getAllBoards();
            return ResponseEntity.ok(boards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 게시글 상세 보기 및 조회수 증가
    @GetMapping("/detail/{boardId}")
    public ResponseEntity<Map<String, Object>> getBoardDetail(@PathVariable UUID boardId,
                                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 조회수 증가
        boardService.incrementBoardView(boardId);

        // 게시글 상세 정보 가져오기
        Optional<BoardEntity> board = boardService.findBoardById(boardId);
        if (board.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 사용자 ID 가져오기
        String userId = (String) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole"); // 사용자 역할도 포함 (admin 등)

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 응답 데이터 구성
        response.put("board", board.get());
        response.put("userId", userId); // 현재 사용자 ID
        response.put("userRole", userRole); // 현재 사용자 역할

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BoardEntity>> searchBoards(@RequestParam String keyword) {
        try {
            List<BoardEntity> results = boardService.searchBoards(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 파일 저장 메서드
    private String saveFile(MultipartFile file) throws IOException {
        // 저장 경로 설정 (uploads/board_photos 폴더)
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/board_photos", fileName);

        // 디렉토리 생성 (없을 경우)
        Files.createDirectories(path.getParent());

        // 파일 저장
        Files.copy(file.getInputStream(), path);

        return path.toString();
    }

    @PutMapping("/edit/{boardId}")
    public ResponseEntity<String> updateBoardWithImages(
            @PathVariable UUID boardId,
            @RequestParam("boardTitle") String boardTitle,
            @RequestParam("boardContent") String boardContent,
            @RequestParam(value = "existingImages", required = false) List<String> existingImages,
            @RequestParam(value = "removedImages", required = false) String removedImagesJson,
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
            HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            Optional<BoardEntity> boardOptional = boardService.findBoardById(boardId);
            if (boardOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }

            BoardEntity board = boardOptional.get();

            // 작성자 확인
            if (!board.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
            }

            // 제목과 내용 업데이트
            board.setBoardTitle(boardTitle);
            board.setBoardContent(boardContent);

            // 기존 이미지 처리
            List<String> updatedImages = existingImages != null ? existingImages : new ArrayList<>();
            board.setImagePath(updatedImages);

            // 삭제된 이미지 처리
            if (removedImagesJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> removedImages = objectMapper.readValue(removedImagesJson, List.class);
                for (String removedImage : removedImages) {
                    Path imagePath = Paths.get("uploads/board_photos", removedImage);
                    Files.deleteIfExists(imagePath); // 물리적 파일 삭제
                }
            }
            // 새 이미지 업로드
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile newImage : newImages) {
                    if (!newImage.isEmpty()) {
                        String savedPath = saveFile(newImage);
                        updatedImages.add(savedPath);
                    }
                }
            }
            board.setImagePath(updatedImages);
            // 저장
            boardService.saveBoard(board);

            return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    /**
     * 게시글과 댓글 삭제
     */
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable UUID boardId, HttpSession session) {
        try {
            // 세션에서 사용자 확인
            String userId = (String) session.getAttribute("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 게시글 삭제 처리
            boardService.deleteBoardWithComments(boardId);

            return ResponseEntity.ok("게시글과 관련 댓글이 모두 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
