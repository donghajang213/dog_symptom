import React, { useEffect, useState } from "react";
import "./UserInfo.css";

const UserInfo = () => {
  const [users, setUsers] = useState([]); // 전체 사용자 정보를 저장할 상태
  const [filteredPosts, setFilteredPosts] = useState([]); // 필터링된 사용자들 상태
  const [selectedUsers, setSelectedUsers] = useState([]); // 선택된 사용자들 상태
  const [searchField, setSearchField] = useState("name"); // 검색 항목(이름, 아이디, 역할 등)
  const [searchQuery, setSearchQuery] = useState(""); // 검색어 상태
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태

  // 사용자 데이터 가져오기
  useEffect(() => {
    async function fetchUsers() {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/userinfo`);
        if (!response.ok) {
          throw new Error("사용자 데이터를 가져오는 데 실패했습니다.");
        }
        const data = await response.json();

        setUsers(data); // 전체 사용자 데이터 설정
        setFilteredPosts(data); // 전체 사용자 데이터로 초기화
      } catch (error) {
        console.error("사용자 데이터 가져오기 실패:", error.message);
      } finally {
        setIsLoading(false); // 로딩 끝
      }
    }

    fetchUsers();
  }, []);

  // 검색 처리
  const handleSearch = () => {
    if (!searchQuery) {
      setFilteredPosts(users); // 검색어가 없으면 전체 사용자 데이터로 설정
      return;
    }

    const lowerKeyword = searchQuery.toLowerCase();
    const filtered = users.filter((user) => {
      if (searchField === "name") {
        return user.name.toLowerCase().includes(lowerKeyword);
      } else if (searchField === "userId") {
        return user.userId.toLowerCase().includes(lowerKeyword);
      } else if (searchField === "userRole") {
        return user.userRole.toLowerCase().includes(lowerKeyword);
      }
      return true;
    });
    setFilteredPosts(filtered); // 필터링된 사용자 설정
  };

  // 선택된 사용자 체크박스 처리
  const handleSelectUser = (userId) => {
    setSelectedUsers((prevSelectedUsers) => {
      if (prevSelectedUsers.includes(userId)) {
        return prevSelectedUsers.filter((id) => id !== userId); // 이미 선택된 경우 제거
      } else {
        return [...prevSelectedUsers, userId]; // 새로 선택된 경우 추가
      }
    });
  };

  // 전체 선택/해제 처리
  const handleSelectAll = () => {
    if (selectedUsers.length === filteredPosts.length) {
      setSelectedUsers([]); // 모두 선택된 상태라면 선택 해제
    } else {
      setSelectedUsers(filteredPosts.map((user) => user.userId)); // 모두 선택
    }
  };

  // 삭제 핸들러
  const handleDeleteUsers = async () => {
    if (window.confirm("선택한 사용자들을 삭제하시겠습니까?")) {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/delete-multiple`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ ids: selectedUsers }), // 선택된 사용자 아이디들
        });

        if (!response.ok) {
          throw new Error("사용자 삭제에 실패했습니다.");
        }

        setUsers(users.filter((user) => !selectedUsers.includes(user.userId)));
        setSelectedUsers([]);
        setFilteredPosts(filteredPosts.filter((user) => !selectedUsers.includes(user.userId))); // 필터링된 사용자 목록에서 삭제
      } catch (error) {
        console.error("사용자 삭제 중 오류 발생:", error.message);
      }
    }
  };

  return (
    <div className="user-info">
      <h2>사용자 정보 관리</h2>

      {/* 검색 항목과 검색어 입력 */}
      <div className="search-inputs">
        <select onChange={(e) => setSearchField(e.target.value)} value={searchField}>
          <option value="name">이름</option>
          <option value="userId">아이디</option>
          <option value="userRole">역할</option>
        </select>
        <input
          type="text"
          placeholder="검색어를 입력하세요"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button onClick={handleSearch} className="search-button">
          검색
        </button>
      </div>

      {/* 삭제 버튼 */}
      <button className="delete-button" onClick={handleDeleteUsers}>
        선택된 사용자 삭제
      </button>

      {/* 전체 선택/해제 체크박스 */}
      <div className="select-all">
        <input
          type="checkbox"
          checked={selectedUsers.length === filteredPosts.length}
          onChange={handleSelectAll}
        />
        <span>전체 선택</span>
      </div>

      {/* 사용자 목록 테이블 */}
      {isLoading ? (
        <p>로딩 중...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>선택</th>
              <th>이름</th>
              <th>아이디</th>
              <th>이메일</th>
              <th>전화번호</th>
              <th>주소</th>
              <th>계좌번호</th>
              <th>등록 날짜</th>
              <th>생년월일</th>
              <th>역할</th>
            </tr>
          </thead>
          <tbody>
            {filteredPosts.map((user) => (
              <tr key={user.userId}>
                <td>
                  <input
                    type="checkbox"
                    checked={selectedUsers.includes(user.userId)}
                    onChange={() => handleSelectUser(user.userId)}
                  />
                </td>
                <td>{user.name}</td>
                <td>{user.userId}</td>
                <td>{user.email}</td>
                <td>{user.phoneNumber}</td>
                <td>{user.address}</td>
                <td>{user.bankAccount}</td>
                <td>{new Date(user.userRegdate).toLocaleDateString()}</td>
                <td>{user.birthDate}</td>
                <td>{user.userRole}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default UserInfo;
