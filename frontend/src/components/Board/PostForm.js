import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./PostForm.css";

const PostForm = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [isLoading, setIsLoading] = useState(false); // 로딩 상태 추가

  // 파일 선택 핸들러
  const handleFileChange = (event) => {
    const selectedFiles = Array.from(event.target.files);
    if (files.length + selectedFiles.length > 10) {
      alert("최대 10개의 파일만 업로드할 수 있습니다.");
      return;
    }
    setFiles([...files, ...selectedFiles]);
  };

  // 서버로 데이터 전송
  const handleSubmit = async (event) => {
    event.preventDefault();

    if (title.trim() === "") {
      alert("제목을 입력해주세요.");
      return;
    }

    if (content.trim().length < 5) {
      alert("내용은 5자 이상 입력해주세요.");
      return;
    }

    // FormData 생성
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    files.forEach((file) => formData.append("photos", file));

    try {
      setIsLoading(true); // 로딩 시작

      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/create`,
        {
          method: "POST",
          body: formData,
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json(); // JSON 응답 파싱
        alert(data.message || "글이 성공적으로 작성되었습니다.");
        navigate("/board/list"); // 글 목록으로 이동
      } else {
        const errorData = await response.json();
        console.error("서버 응답 에러:", errorData);
        alert("글 작성 중 오류가 발생했습니다.");
      }
    } catch (error) {
      console.error("글 작성 중 오류 발생:", error);
      alert("글 작성 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false); // 로딩 종료
    }
  };

  return (
    <div className="post-form-container">
      <h2>글 작성</h2>
      <form onSubmit={handleSubmit}>
        {/* 제목 입력 */}
        <div className="form-group">
          <label htmlFor="title">제목</label>
          <input
            type="text"
            id="title"
            placeholder="제목을 입력해주세요"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </div>

        {/* 내용 입력 */}
        <div className="form-group">
          <label htmlFor="content">내용</label>
          <textarea
            id="content"
            placeholder="5자 이상의 글 내용을 입력해주세요"
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
        </div>

        {/* 파일 업로드 */}
        <div className="form-group">
          <label htmlFor="file-upload">사진 업로드</label>
          <input
            type="file"
            id="file-upload"
            multiple
            onChange={handleFileChange}
          />
          <div className="file-info">
            {files.length > 0 && (
              <p>{files.length}개의 파일이 선택되었습니다. (최대 10개)</p>
            )}
          </div>
        </div>

        {/* 버튼 그룹 */}
        <div className="button-group">
          <button
            type="button"
            className="list-button"
            onClick={() => navigate("/board/list")}
          >
            글 목록
          </button>
          <button type="submit" className="submit-button">
            {isLoading ? "등록 중..." : "글 등록"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PostForm;
