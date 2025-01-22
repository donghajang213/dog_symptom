import React, { useEffect, useContext, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { UserContext } from "../../contexts/UserContext";
import "./Header.css";

function Header() {
  const { user, setUser } = useContext(UserContext);
  const [requestCount, setRequestCount] = useState(0);  // Add requestCount state
  const navigate = useNavigate();
  const location = useLocation();
  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

  const checkSession = async () => {
    try {
      console.log("checkSession 시작");
      const response = await fetch(`${API_BASE_URL}/user/me`, {
        method: "GET",
        credentials: "include",
      });

      if (response.ok) {
        const userData = await response.json();
        console.log("사용자 데이터: ", userData); // 사용자 데이터 확인

        // user 객체에서 userRole과 userId 추출
        const { user } = userData;
        // 구조분해 할당을 사용하여 userRole 값 추출
        const { userRole, userId } = user;
        console.log("userRole 값: ", userRole);
        setUser(userData);

        if (userRole === "VET") {
          console.log("userRole은 VET입니다.") // 추가
          sessionStorage.setItem("vetId", userId); // Save vetId for future use
          console.log("fetchRequestCount 호출");
          fetchRequestCount(userId);  // Fetch pending request count for VET users
        } else{
          console.log("userRole은 VET가 아닙니다");
        }
      } else {
        setUser(null);
      }
    } catch (error) {
      console.error("세션 확인 오류:", error);
      setUser(null);
    }
  };

  useEffect(() => {
    console.log("useEffect 실행됨");
    checkSession();
  }, []);

  const fetchRequestCount = async (userId) => {
    try {
      console.log("API 호출 시작: ", `${API_BASE_URL}/consultations/${userId}/pendingRequestCount`);
      const response = await fetch(`${API_BASE_URL}/consultations/${userId}/pendingRequestCount`);
      if (response.ok) {
        const count = await response.json();
        console.log("Pending count: ", count);
        setRequestCount(count);
      } else {
        const errorText = await response.text();
        console.error("미확인 상담 요청 수를 가져오는 데 실패했습니다:", errorText);
      }
    } catch (error) {
      console.error("요청 수 가져오기 실패:", error);
    }
  };





  const handleLogout = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/logout`, {
        method: "POST",
        credentials: "include",
        redirect: "manual",
      });

      if (response.ok) {
        sessionStorage.clear();
        setUser(null);
        navigate("/");
      } else {
        console.error("로그아웃 실패:", response);
        alert("로그아웃에 실패했습니다.");
      }
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
      alert("서버 오류가 발생했습니다.");
    }
  };

  const handleNavigateToPage = (e, path) => {
    e.preventDefault();

    if (location.pathname.startsWith(path) && path === "/mypage") {
      navigate(`/mypage?refresh=${new Date().getTime()}`, { replace: true });
    } else if (path === location.pathname) {
      navigate(`${path}?refresh=${new Date().getTime()}`, { replace: true });
    } else {
      navigate(path, { replace: true });
    }
  };

  return (
      <header className="header">
        <div className="logo">
          <Link to="/">아토피아</Link>
        </div>
        <nav>
          <ul className="nav-list">
            <li>
              <Link to="/paypage" onClick={(e) => handleNavigateToPage(e, '/paypage')}>
                뼈다귀 구매
              </Link>
            </li>
            <li>
              <Link to="/board/list" onClick={(e) => handleNavigateToPage(e, "/board/list")}>
                라운지
              </Link>
            </li>
            <li>
              <Link to="/hospital" onClick={(e) => handleNavigateToPage(e, "/hospital")}>
                근처 병원
              </Link>
            </li>
            {user && user.userRole === "CUSTOMER" && (
                <li>
                  <Link to="/clinic/select-pet" onClick={(e) => handleNavigateToPage(e, "/clinic/select-pet")}>
                    클리닉
                  </Link>
                </li>
            )}
            <li>
              <Link to="/store" onClick={(e) => handleNavigateToPage(e, "/store")}>
                스토어
              </Link>
            </li>
            {!user || user.userRole !== "admin" ? (
                <li>
                  {user ? (
                      <Link to="/mypage" onClick={(e) => handleNavigateToPage(e, "/mypage")}>
                        마이페이지
                      </Link>
                  ) : (
                      <Link to="/login" onClick={() => alert("로그인이 필요합니다.")}>
                        마이페이지
                      </Link>
                  )}
                </li>
            ) : null}
            {user && user.userRole === "admin" && (
                <li>
                  <Link to="/admin" onClick={(e) => handleNavigateToPage(e, "/admin")}>
                    관리자
                  </Link>
                </li>
            )}
            {user && user.userRole === "VET" && (
                <li>
                  <Link to="/clinic/request" onClick={(e) => handleNavigateToPage(e, "/clinic/request")}>
                    상담 내역 <span className="badge">{requestCount > 0 ? requestCount : null}</span>
                  </Link>
                </li>
            )}
            {user && (user.userRole === "CUSTOMER" || user.userRole === "VET") && (
                <li>
                  <Link to="/chat" onClick={(e) => handleNavigateToPage(e, "/chat")}>
                    대화창
                  </Link>
                </li>
            )}
            <li>
              {user ? (
                  <Link to="#" onClick={handleLogout}>
                    로그아웃
                  </Link>
              ) : (
                  <Link to="/login">로그인</Link>
              )}
            </li>
          </ul>
        </nav>
      </header>
  );
}

export default Header;
