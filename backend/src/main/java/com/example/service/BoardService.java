package com.example.service;

import com.example.entity.BoardEntity;
import com.example.entity.CommentEntity;
import com.example.repository.BoardRepository;
import com.example.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;


    public void saveBoard(BoardEntity board) {
        boardRepository.save(board);
    }

    public List<BoardEntity> getAllBoards() {
        return boardRepository.findAll();
    }


    public Optional<BoardEntity> findBoardById(UUID boardId) {
        return boardRepository.findById(boardId);
    }


    public void incrementBoardView(UUID boardId) {
        // 게시글 조회
        Optional<BoardEntity> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isPresent()) {
            BoardEntity board = optionalBoard.get();
            board.setBoardView(board.getBoardView() + 1); // 조회수 증가
            boardRepository.save(board); // 변경 사항 저장
        }
    }

    // 제목 또는 내용 검색
    public List<BoardEntity> searchBoards(String keyword) {
        return boardRepository.searchByTitleOrContent(keyword);
    }

    // 게시글과 관련 댓글 삭제
    public void deleteBoardWithComments(UUID boardId) {
        // 게시글 삭제
        boardRepository.deleteById(boardId);

        // 관련 댓글 삭제
        commentService.deleteCommentsByBoardId(boardId);
    }

}
