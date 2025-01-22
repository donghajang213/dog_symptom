import React, { useState, useEffect } from "react";
import "./BannerManagement.css";

function BannerManagement() {
  const [banners, setBanners] = useState([]); // 배너 데이터 상태
  const [newBanner, setNewBanner] = useState({
    redirectUrl: "",
    priority: 1,
    startDate: "",
    endDate: "",
  });
  const [selectedFile, setSelectedFile] = useState(null); // 파일 상태 추가
  const [editingBannerId, setEditingBannerId] = useState(null);
  const [error, setError] = useState(null); // 오류 상태 관리

  const API_URL = `${process.env.REACT_APP_API_BASE_URL}/banners`; // Cassandra와 연결된 백엔드 API URL

  // Cassandra에서 데이터 가져오기
  useEffect(() => {
    const fetchBanners = async () => {
      try {
        const response = await fetch(API_URL);
        if (!response.ok) {
          throw new Error("배너 데이터를 가져오는 데 실패했습니다.");
        }
        const data = await response.json();

        // 우선순위로 정렬
        const sortedBanners = data.sort((a, b) => a.priority - b.priority);
        setBanners(sortedBanners);
      } catch (error) {
        console.error("배너 데이터를 가져오는 데 실패:", error);
        setError(error.message);
      }
    };

    fetchBanners();
  }, [API_URL]); // API URL을 의존성으로 추가하여 업데이트 시 반영

  // 입력값 변경 처리
  const handleInputChange = (e) => {
    setNewBanner({ ...newBanner, [e.target.name]: e.target.value });
  };

  // 파일 입력 처리
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]); // 파일 선택 상태 설정
  };

  // 새 배너 추가 또는 수정
  const handleSubmit = async () => {
    if (!selectedFile || !newBanner.redirectUrl || !newBanner.startDate || !newBanner.endDate) {
      alert("모든 필드를 입력해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("imageFile", selectedFile); // 파일 추가
    formData.append("redirectUrl", newBanner.redirectUrl);
    formData.append("priority", newBanner.priority);
    formData.append("startDate", newBanner.startDate);
    formData.append("endDate", newBanner.endDate);

    try {
      const method = editingBannerId ? "PUT" : "POST";
      const url = editingBannerId ? `${API_URL}/admin/${editingBannerId}` : `${API_URL}/admin`;

      const response = await fetch(url, {
        method,
        body: formData, // FormData를 직접 전송
      });

      if (!response.ok) {
        throw new Error("배너 저장에 실패했습니다.");
      }

      const updatedBanner = await response.json();

      if (editingBannerId) {
        setBanners((prevBanners) =>
          prevBanners.map((banner) =>
            banner.bannerId === editingBannerId ? updatedBanner : banner
          )
        );
        setEditingBannerId(null);
      } else {
        setBanners((prevBanners) => [...prevBanners, updatedBanner]);
      }

      setNewBanner({
        redirectUrl: "",
        priority: 1,
        startDate: "",
        endDate: "",
      });
      setSelectedFile(null); // 파일 상태 초기화
      alert("배너가 성공적으로 추가되었습니다!");
    } catch (error) {
      console.error("배너 저장 실패:", error);
      setError(error.message);
    }
  };

  // 배너 수정
  const handleEditBanner = (bannerId) => {
    const bannerToEdit = banners.find((banner) => banner.bannerId === bannerId);
    if (bannerToEdit) {
      setNewBanner({
        redirectUrl: bannerToEdit.redirectUrl,
        priority: bannerToEdit.priority,
        startDate: bannerToEdit.startDate,
        endDate: bannerToEdit.endDate,
      });
      setEditingBannerId(bannerId);
    }
  };

  // 배너 삭제
  const handleDeleteBanner = async (bannerId) => {
    if (!window.confirm("정말로 삭제하시겠습니까?")) return;

    try {
      const response = await fetch(`${API_URL}/admin/${bannerId}`, { method: "DELETE" });
      if (!response.ok) {
        throw new Error("배너 삭제에 실패했습니다.");
      }

      setBanners((prevBanners) => prevBanners.filter((banner) => banner.bannerId !== bannerId));
    } catch (error) {
      console.error("배너 삭제 실패:", error);
      setError(error.message);
    }
  };

  return (
    <div className="banner-management">
      <h3>배너 관리</h3>

      {/* 오류 메시지 표시 */}
      {error && <p className="error-message">오류: {error}</p>}

      <div className="banner-form">
        <input type="file" onChange={handleFileChange} /> {/* 파일 입력 필드 */}
        <input
          type="text"
          name="redirectUrl"
          placeholder="클릭 시 이동할 URL"
          value={newBanner.redirectUrl}
          onChange={handleInputChange}
        />
        <input
          type="number"
          name="priority"
          placeholder="우선순위"
          value={newBanner.priority}
          onChange={handleInputChange}
        />
        <input
          type="date"
          name="startDate"
          placeholder="시작 날짜"
          value={newBanner.startDate}
          onChange={handleInputChange}
        />
        <input
          type="date"
          name="endDate"
          placeholder="종료 날짜"
          value={newBanner.endDate}
          onChange={handleInputChange}
        />
        <button onClick={handleSubmit}>
          {editingBannerId ? "수정 저장" : "추가"}
        </button>
      </div>

      <div className="banner-list">
        {banners.map((banner) => (
          <div key={banner.bannerId} className="banner-card">
            <a href={banner.redirectUrl} target="_blank" rel="noopener noreferrer">
              <img src={banner.imageUrl} alt="배너 이미지" />
            </a>
            <p>URL: {banner.redirectUrl || "URL 없음"}</p>
            <p>우선순위: {banner.priority}</p>
            <p>시작 날짜: {banner.startDate || "시작 날짜 없음"}</p>
            <p>종료 날짜: {banner.endDate || "종료 날짜 없음"}</p>
            <div className="button-group">
              <button onClick={() => handleEditBanner(banner.bannerId)}>수정</button>
              <button onClick={() => handleDeleteBanner(banner.bannerId)}>삭제</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default BannerManagement;