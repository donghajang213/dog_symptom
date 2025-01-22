import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./PostList.css";

const PostList = () => {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]); // 전체 게시글 데이터
  const [filteredPosts, setFilteredPosts] = useState([]); // 검색된 게시글 데이터
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태
  const [searchKeyword, setSearchKeyword] = useState(""); // 검색 키워드
  const [searchType, setSearchType] = useState("title"); // 검색 기준 (제목, 내용)
  const [sortOrder, setSortOrder] = useState("latest"); // 정렬 기준 (최신순, 조회순)

  // 서버에서 게시글 데이터를 가져오는 함수
  const fetchPosts = async () => {
    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/board/list`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (response.ok) {
        const data = await response.json();
        setPosts(data);
        setFilteredPosts(data); // 검색 결과 초기화
      } else {
        console.error("응답 상태 코드:", response.status);
      }
    } catch (error) {
      console.error("서버 요청 중 오류 발생:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // 검색 기능
  const handleSearch = () => {
    const lowerKeyword = searchKeyword.toLowerCase();
    const filtered = posts.filter((post) =>
      searchType === "title"
        ? post.boardTitle.toLowerCase().includes(lowerKeyword)
        : post.boardContent.toLowerCase().includes(lowerKeyword)
    );
    setFilteredPosts(filtered);
  };

  // 정렬 기능
  const handleSort = (order) => {
    const sorted = [...filteredPosts].sort((a, b) => {
      if (order === "latest") {
        return new Date(b.boardDate) - new Date(a.boardDate); // 최신순
      } else if (order === "views") {
        return b.boardView - a.boardView; // 조회순
      }
      return 0;
    });
    setFilteredPosts(sorted);
    setSortOrder(order); // 정렬 기준 업데이트
  };

  // 현재 시간과 비교하여 최근 24시간 이내 등록된 글인지 확인하는 함수
  const isNewPost = (postDate) => {
    const postTime = new Date(postDate).getTime();
    const now = new Date().getTime();
    return now - postTime <= 24 * 60 * 60 * 1000; // 24시간 이내인지 확인
  };

  // 컴포넌트가 마운트될 때 데이터 가져오기
  useEffect(() => {
    fetchPosts();
  }, []);

  return (
    <div className="post-list-container">
      {/* 검색 및 정렬 영역 */}
      <div className="top-bar">
        <div className="search-container">
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className="search-type"
          >
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            className="search-input"
          />
          <button onClick={handleSearch} className="search-button">
            검색
          </button>
        </div>

        {/* 정렬 영역 (라디오 버튼) */}
        <div className="sort-container">
          <label>
            <input
              type="radio"
              name="sortOrder"
              value="latest"
              checked={sortOrder === "latest"}
              onChange={() => handleSort("latest")}
            />
            최신순
          </label>
          <label>
            <input
              type="radio"
              name="sortOrder"
              value="views"
              checked={sortOrder === "views"}
              onChange={() => handleSort("views")}
            />
            조회순
          </label>
        </div>
      </div>

      {/* 글쓰기 버튼 */}
      <div className="write-button-container">
        <button
          className="write-button"
          onClick={() => navigate("/board/form")}
        >
          글쓰기
        </button>
      </div>

      {/* 게시글 리스트 */}
      <div className="posts-container">
        <h2>커뮤니티</h2>
        {isLoading ? (
          <p>로딩 중...</p> // 로딩 중 상태 표시
        ) : filteredPosts.length > 0 ? (
          filteredPosts.map((post) => (
            <div key={post.boardId} className="post-item">
              <h3
                className="post-title"
                onClick={() => navigate(`/board/detail/${post.boardId}`)}
              >
                {isNewPost(post.boardDate) && (
                  <span className="new-label">NEW</span>
                )}
                {post.boardTitle}
              </h3>
              <p className="post-content">{post.boardContent}</p>
              <div className="post-info">
                <span>{post.userId}</span>
                <span>{new Date(post.boardDate).toLocaleDateString()}</span>
                <span>조회수 {post.boardView}</span>
              </div>
            </div>
          ))
        ) : (
          <p>검색 결과가 없습니다.</p>
        )}
      </div>
    </div>
  );
};

export default PostList;
