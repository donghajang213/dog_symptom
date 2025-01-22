import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useLocation } from "react-router-dom";
// import "./PetSelect.css"; // 스타일 파일 추가 필요

function PetSelect() {
  const [user, setUser] = useState(null); // 사용자 정보 상태
  const [pets, setPets] = useState([]); // 펫 정보 상태
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태
  const navigate = useNavigate();
  const location = useLocation();
  const selectedPet = location.state?.selectedPet; // 선택된 반려동물 정보 가져오기

  const calculateAge = (birthDate) => {
    const today = new Date(); // 현재 날짜
    const birth = new Date(birthDate); // 생일 날짜

    let age = today.getFullYear() - birth.getFullYear(); // 현재 연도에서 태어난 연도 뺌
    const monthDiff = today.getMonth() - birth.getMonth(); // 월 차이 계산

    // 생일이 아직 안 지난 경우 나이를 하나 줄임
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }

    return age;
  };

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/me`, {
          method: "GET",
          credentials: "include",
        });

        if (response.ok) {
          const data = await response.json();
          console.log("API 응답 데이터:", data);
          setUser(data.user); // 사용자 정보 설정
          setPets(data.pets || []); // 펫 정보 설정
        } else if (response.status === 401) {
          console.error("인증 실패: 로그인 페이지로 이동");
          navigate("/login"); // 인증 실패 시 로그인 페이지로 이동
        } else {
          console.error("API 호출 실패:", response.status);
        }
      } catch (error) {
        console.error("API 호출 중 오류 발생:", error);
      } finally {
        setIsLoading(false); // 로딩 상태 해제
      }
    };

    fetchUserData();
  }, [navigate]);

  const handleSelectPet = (pet) => {
    alert(`${pet.name}를 선택하셨습니다!`);
    // 선택한 펫 데이터를 navigate의 state로 전달
    navigate("/clinic/photo", { state: { selectedPet: pet } });
  };

  useEffect(() => {
    if (!isLoading && (!pets || pets.length === 0)) {
      alert("등록된 반려동물이 없습니다. 마이페이지에서 반려동물을 등록해주세요.");
      navigate("/mypage");
    }
  }, [isLoading, pets, navigate]);

  if (isLoading) {
    return <div>로딩 중...</div>; // 로딩 중 표시
  }

  return (
      <div className="pet-select-container">
        <h2>펫을 선택해주세요</h2>
        <div className="pet-list">
          {pets.map((pet) => (
              <div
                  key={pet.petId}
                  className="pet-card"
                  onClick={() => handleSelectPet(pet)} // 펫 선택 시 호출
              >
                <img
                    src={
                      pet.petImage && typeof pet.petImage === "string"
                          ? `${process.env.REACT_APP_API_BASE_URL}/${pet.petImage.replace(/\\/g, "/")}`
                          : "/images/default_pet.jpg" // 기본 이미지
                    }
                    alt={pet.name}
                    className="pet-photo"
                />
                <div className="pet-info">
                  <h3>{pet.name}</h3>
                  <p>나이: {pet.birthDate ? `${calculateAge(pet.birthDate)}살` : "정보 없음"}</p>
                  <p>품종: {pet.breed || "정보 없음"}</p>
                </div>
              </div>
          ))}
        </div>
      </div>
  );
}

export default PetSelect;
