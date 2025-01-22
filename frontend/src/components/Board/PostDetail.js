import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./PostDetail.css";

const BoardDetail = () => {
  const { boardId } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [commentImage, setCommentImage] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [loggedInUserId, setLoggedInUserId] = useState("");

  // 게시글 데이터 가져오기
  const fetchBoardDetail = async () => {
    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/detail/${boardId}`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json();
        setBoard(data.board);
        setLoggedInUserId(data.userId);
      } else {
        console.error("게시글 데이터를 가져오는 데 실패했습니다.");
      }
    } catch (error) {
      console.error("서버 요청 중 오류 발생:", error);
    }
  };

  // 댓글 데이터 가져오기
  const fetchComments = async () => {
    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/comments/list/${boardId}`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json();
        const sortedData = data.sort(
          (a, b) => new Date(b.commentTime) - new Date(a.commentTime)
        );
        setComments(sortedData);
      } else {
        console.error("댓글 데이터를 가져오는 데 실패했습니다.");
      }
    } catch (error) {
      console.error("서버 요청 중 오류 발생:", error);
    }
  };

  // 댓글 작성
  const handleCommentSubmit = async (e) => {
    e.preventDefault();

    if (!newComment.trim()) {
      alert("댓글 내용을 입력해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("boardId", boardId);
    formData.append("content", newComment);
    if (commentImage) {
      formData.append("image", commentImage);
    }

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/comments/create`,
        {
          method: "POST",
          body: formData,
          credentials: "include",
        }
      );

      if (response.ok) {
        alert("댓글이 성공적으로 등록되었습니다.");
        setNewComment("");
        setCommentImage(null);
        fetchComments(); // 댓글 목록 갱신
      } else {
        alert("댓글 등록에 실패했습니다.");
      }
    } catch (error) {
      console.error("댓글 작성 중 오류 발생:", error);
    }
  };

  // 댓글 삭제
  const handleDeleteComment = async (boardId, commentId) => {
      if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

      try {
          const response = await fetch(
              `${process.env.REACT_APP_API_BASE_URL}/comments/delete/${boardId}/${commentId}`,
              {
                  method: "DELETE",
                  credentials: "include",
              }
          );

          if (response.ok) {
              alert("댓글이 삭제되었습니다.");
              fetchComments(); // 댓글 목록 갱신
          } else {
              const errorText = await response.text();
              alert(`댓글 삭제 실패: ${errorText}`);
          }
      } catch (error) {
          console.error("댓글 삭제 요청 중 오류 발생:", error);
      }
  };

  // 게시글 삭제
  const handleDeleteBoard = async () => {
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/delete/${boardId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (response.ok) {
        alert("게시글이 삭제되었습니다.");
        navigate("/board/list"); // 목록으로 이동
      } else {
        alert("게시글 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("게시글 삭제 요청 중 오류 발생:", error);
    }
  };

  // 초기 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      await fetchBoardDetail();
      await fetchComments();
      setIsLoading(false);
    };
    fetchData();
  }, [boardId]);

  if (isLoading) {
    return <p>로딩 중...</p>;
  }

  if (!board) {
    return <p>게시글을 불러올 수 없습니다.</p>;
  }

  const getRelativeTime = (date) => {
    const now = new Date();
    const diff = Math.floor((now - new Date(date)) / 1000);

    if (diff < 60) return "방금 전";
    if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
    return `${Math.floor(diff / 86400)}일 전`;
  };

  return (
    <div className="board-detail-container">
      <div className="floating-button">
        <button onClick={() => navigate("/board/list")}>글 목록으로</button>
      </div>

      <h1>{board.boardTitle}</h1>
      <p>{board.boardContent}</p>
      <div className="board-images">
        {board.imagePath &&
          board.imagePath.map((path, index) => (
            <img
              key={index}
              src={`${process.env.REACT_APP_API_BASE_URL}/${path}`}
              alt={`이미지 ${index + 1}`}
            />
          ))}
      </div>
      <div className="board-info">
        <span>작성자: {board.userId}</span>
        <span>{new Date(board.boardDate).toLocaleDateString()}</span>
        <span>조회수: {board.boardView}</span>
      </div>

      {loggedInUserId === board.userId && (
        <div className="edit-delete-buttons">
          <button
            onClick={() => navigate(`/board/edit/${boardId}`)}
            className="edit-button"
          >
            수정
          </button>
          <button onClick={handleDeleteBoard} className="delete-button">
            삭제
          </button>
        </div>
      )}

      <div className="comments-section">
        <h2>댓글</h2>
        {comments.length > 0 ? (
          comments.map((comment) => (
            <div key={comment.commentId} className="comment-item">
              <div className="comment-info">
                <span>작성자: {comment.userId}</span>
                <span>{getRelativeTime(comment.commentTime)}</span>
              </div>
              <p>{comment.commentContent}</p>
              {comment.commentImagePath && (
                <img
                  src={`${process.env.REACT_APP_API_BASE_URL}/${comment.commentImagePath}`}
                  alt="댓글 이미지"
                  className="comment-image"
                />
              )}
              {loggedInUserId === comment.userId && (
                <button
                    className="delete-comment-button"
                    onClick={() => handleDeleteComment(boardId, comment.commentId)}
                >
                    삭제
                </button>
              )}
            </div>
          ))
        ) : (
          <p>댓글이 없습니다.</p>
        )}
      </div>

      <form onSubmit={handleCommentSubmit} className="comment-form">
        <textarea
          placeholder="댓글을 입력하세요"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        ></textarea>
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setCommentImage(e.target.files[0])}
        />
        <button type="submit">댓글 작성</button>
      </form>
    </div>
  );
};

export default BoardDetail;
