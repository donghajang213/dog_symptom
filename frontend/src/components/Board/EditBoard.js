import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const EditBoard = () => {
  const { boardId } = useParams();
  const navigate = useNavigate();
  const [boardTitle, setBoardTitle] = useState("");
  const [boardContent, setBoardContent] = useState("");
  const [existingImages, setExistingImages] = useState([]);
  const [newImages, setNewImages] = useState([]);
  const [removedImages, setRemovedImages] = useState([]); // 삭제된 이미지 목록

  useEffect(() => {
    const fetchBoardData = async () => {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/detail/${boardId}`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json();
        setBoardTitle(data.board.boardTitle);
        setBoardContent(data.board.boardContent);
        setExistingImages(data.board.imagePath || []);
      }
    };

    fetchBoardData();
  }, [boardId]);

  const handleRemoveImage = (imagePath) => {
    setRemovedImages([...removedImages, imagePath]); // 삭제된 이미지 추가
    setExistingImages(existingImages.filter((img) => img !== imagePath)); // 기존 이미지에서 제거
  };

  const handleSave = async () => {
    const formData = new FormData();
    formData.append("boardTitle", boardTitle);
    formData.append("boardContent", boardContent);

    // 기존 이미지 경로 추가
    existingImages.forEach((imagePath) => {
      formData.append("existingImages", imagePath);
    });

    // 삭제된 이미지 경로 JSON으로 추가
    if (removedImages.length > 0) {
      formData.append("removedImages", JSON.stringify(removedImages));
    }

    // 새 이미지 파일 추가
    newImages.forEach((newImage) => {
      formData.append("newImages", newImage);
    });

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/edit/${boardId}`,
        {
          method: "PUT",
          body: formData,
          credentials: "include",
        }
      );

      if (response.ok) {
        alert("게시글이 성공적으로 수정되었습니다.");
        navigate(`/board/detail/${boardId}`);
      } else {
        const errorText = await response.text();
        alert(`수정 실패: ${errorText}`);
      }
    } catch (error) {
      console.error("수정 요청 중 오류 발생:", error);
    }
  };

  return (
    <div>
      <h2>게시글 수정</h2>
      <div>
        <label>제목</label>
        <input
          type="text"
          value={boardTitle}
          onChange={(e) => setBoardTitle(e.target.value)}
        />
      </div>
      <div>
        <label>내용</label>
        <textarea
          value={boardContent}
          onChange={(e) => setBoardContent(e.target.value)}
        ></textarea>
      </div>
      <div>
        <h3>기존 이미지</h3>
        {existingImages.map((img, index) => (
          <div key={index} style={{ display: "inline-block", margin: "10px" }}>
            <img
              src={`${process.env.REACT_APP_API_BASE_URL}/${img.replace(/\\/g, "/")}`}
              alt={`기존 이미지 ${index + 1}`}
              style={{ maxWidth: "200px" }}
            />
            <button onClick={() => handleRemoveImage(img)}>X</button>
          </div>
        ))}
      </div>
      <div>
        <label>새 이미지 추가</label>
        <input
          type="file"
          multiple
          accept="image/*"
          onChange={(e) => setNewImages([...e.target.files])}
        />
      </div>
      <button onClick={handleSave}>저장</button>
      <button onClick={() => navigate(`/board/detail/${boardId}`)}>취소</button>
    </div>
  );
};

export default EditBoard;
