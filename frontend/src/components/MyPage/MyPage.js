import React, { useState, useContext, useEffect } from "react";
import { UserContext } from "../../contexts/UserContext";
import { useNavigate } from "react-router-dom";
import PetRegistrationModal from "./PetRegistrationModal/PetRegistrationModal";
import PetProfileModal from "./PetProfileModal/PetProfileModal";
import PetEditModal from "./PetEditModal/PetEditModal";
import "./MyPage.css";

function MyPage() {
    const { user, setUser } = useContext(UserContext);
    const [pets, setPets] = useState([]);
    const [editingPet, setEditingPet] = useState(null);
    const [selectedPet, setSelectedPet] = useState(null);
    const [isProfileVisible, setIsProfileVisible] = useState(false);
    const [isEditVisible, setIsEditVisible] = useState(false);
    const [isRegistrationVisible, setIsRegistrationVisible] = useState(false);
    const [isUserInfoVisible, setIsUserInfoVisible] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/me`, {
                    method: "GET",
                    credentials: "include",
                });

                if (response.ok) {
                    const data = await response.json();
                    setUser(data.user);
                    setPets(data.pets || []);
                } else if (response.status === 401) {
                    navigate("/login"); // 인증 실패 시 로그인 페이지로 이동
                } else {
                    console.error("API 호출 실패:", response.status);
                }
            } catch (error) {
                console.error("API 호출 중 오류 발생:", error);
            }
        };

        fetchUserData();
    }, [setUser, setPets, navigate]);

    useEffect(() => {
        // 첫 화면으로 돌아가기 위한 로직 추가
        if (isEditing) {
            setIsEditing(false);
            console.log("정보 수정 화면에서 첫 화면으로 돌아갑니다.");
            navigate('/mypage'); // 마이페이지 첫 화면으로 이동
        }
    }, [isEditing, navigate]);

    if (!user) {
        return <div>로딩 중...</div>;
    }

    const handleViewPet = (pet) => {
        if (!pet) {
            console.error("선택된 반려동물 데이터가 없습니다.");
            return;
        }
        setSelectedPet(pet);
        setIsProfileVisible(true);
    };

    const handleEditPet = (pet) => {
        setEditingPet(pet);
        setIsProfileVisible(false);
        setIsEditVisible(true);
    };

    const handleAddPet = () => {
        setIsProfileVisible(false);
        setIsRegistrationVisible(true);
    };

    const handleSavePet = (updatedPet) => {
        setPets((prevPets) =>
            prevPets.map((pet) => (pet.petId === updatedPet.petId ? updatedPet : pet))
        );
        setSelectedPet((prevSelectedPet) =>
            prevSelectedPet && prevSelectedPet.petId === updatedPet.petId
                ? updatedPet
                : prevSelectedPet
        );
        setIsEditVisible(false);
    };

    const handleAddNewPet = (newPet) => {
        setPets([...pets, newPet]);
        setIsRegistrationVisible(false);
    };

    const handleDeletePet = async (petId) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/pets/delete/${petId}`,{
                method: "DELETE",
                credentials: "include",
            });

            if (response.ok) {
                setPets(pets.filter((pet) => pet.petId !== petId));
                setIsProfileVisible(false);
            } else {
                const errorText = await response.text();
                alert("삭제 실패: " + errorText);
            }
        } catch (error) {
            console.error("삭제 요청 중 오류 발생:", error);
        }
    };

    const handleCloseProfile = () => {
        setSelectedPet(null);
        setIsProfileVisible(false);
    };

    const handleCloseEdit = () => {
        setIsEditVisible(false);
        setEditingPet(null);
        if (selectedPet) {
            setIsProfileVisible(true);
        }
    };

    const handleSaveUserInfo = async (e) => {
        e.preventDefault();
        // FormData 생성
            const formData = new FormData();

            // JSON 데이터를 FormData에 추가
            formData.append("userInfo", JSON.stringify({
                userId: user.userId,
                name: e.target.name.value,
                birthdate: e.target.birthdate.value,
                email: e.target.email.value,
                phoneNumber: e.target.phoneNumber.value,
                address: e.target.address.value,
                detailedAddress: e.target.detailedAddress.value,
            }));

            // 이미지 파일 추가 (파일 선택된 경우)
            const imageFile = e.target.image?.files[0];
            if (imageFile) {
                formData.append("image", imageFile);
            }

        try {
                const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/update`, {
                    method: "PUT",
                    body: formData, // Content-Type은 자동으로 설정
                    credentials: "include",
                });

            if (response.ok) {
                const data = await response.json();
                setUser(data);
                alert("회원 정보가 성공적으로 저장되었습니다.");
                setIsUserInfoVisible(false);
                navigate('/mypage'); // 마이페이지 첫 화면으로 이동
                await fetchUserData(); // 회원 정보 및 펫 정보 다시 로드
            } else {
                console.error("회원 정보 저장 실패:", response.status);
                alert("회원 정보 저장 중 오류가 발생했습니다.");
            }
        } catch (error) {
            console.error("API 호출 중 오류 발생:", error);
            alert("회원 정보 저장 중 오류가 발생했습니다.");
        }
    };

    const fetchUserData = async () => {
        try {
          const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/me`, {
            method: "GET",
            credentials: "include",
          });

          if (response.ok) {
            const data = await response.json();
            setUser(data.user);
            setPets(data.pets || []);
          } else {
            console.error("유저 정보 로드 실패:", response.status);
          }
        } catch (error) {
          console.error("API 호출 중 오류 발생:", error);
        }
      };

    const handleDeleteUser = async () => {
        const confirmDelete = window.confirm("정말로 회원 탈퇴를 진행하시겠습니까?");
        if (!confirmDelete) return;

        try {
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/delete`, {
                method: "DELETE",
                credentials: "include",
            });

            if (response.ok) {
                alert("회원 탈퇴가 완료되었습니다.");
                setUser(null);
                navigate('/'); // 홈 화면으로 이동
            } else {
                console.error("회원 탈퇴 실패:", response.status);
                alert("회원 탈퇴 중 오류가 발생했습니다.");
            }
        } catch (error) {
            console.error("API 호출 중 오류 발생:", error);
            alert("회원 탈퇴 중 오류가 발생했습니다.");
        }
    };

    const handleToggleUserInfo = () => {
        setIsUserInfoVisible(!isUserInfoVisible);
        setIsEditing(true); // isEditing 상태 업데이트
    };

    return (
        <div className="mypage-container">
            <div className="mypage-sidebar">
                <ul className="mypage-categories">
                    <li>
                        <h3>나의 반려동물</h3>
                        <ul>
                            <li>건강 다이어리</li>
                        </ul>
                    </li>
                    <li>
                        <h3>상담 관리</h3>
                        <ul>
                            <li>나의 상담</li>
                            <li>상담권 관리</li>
                        </ul>
                    </li>
                    <li>
                        <h3>나의 쇼핑 정보</h3>
                        <ul>
                            <li>주문/배송</li>
                            <li>교환/반품</li>
                            <li>찜한 제품</li>
                            <li>제품 리뷰</li>
                        </ul>
                    </li>
                    <li>
                        <h3>나의 정보</h3>
                        <ul>
                            <li onClick={handleToggleUserInfo} style={{ cursor: "pointer" }}>
                                회원 정보 관리
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
            <div className="mypage-main">
                <div className="mypage-header">
                    <h2>{user.name}님의 마이페이지</h2>
                    <button className="add-pet-button" onClick={handleAddPet}>
                        + 반려동물 등록
                    </button>
                </div>
                {isUserInfoVisible ? (
                    <div className="user-info-section">
                        <h3>회원 정보</h3>
                        <form onSubmit={handleSaveUserInfo}>
                            <div className="form-group">
                                <label>이름</label>
                                <input type="text" name="name" defaultValue={user?.name || ''} />
                            </div>
                            <div className="form-group">
                                <label>생년월일</label>
                                <input type="date" name="birthdate" defaultValue={user?.birthDate || ''} />
                            </div>
                            <div className="form-group">
                                <label>이메일</label>
                                <input type="email" name="email" defaultValue={user?.email || ''} />
                            </div>
                            <div className="form-group">
                                <label>핸드폰 번호</label>
                                <input type="text" name="phoneNumber" defaultValue={user?.phoneNumber || ''} />
                            </div>
                            <div className="form-group">
                                <label>주소</label>
                                <input type="text" name="address" defaultValue={user?.address || ''} />
                            </div>
                            <div className="form-group">
                                <label>상세 주소</label>
                                <input type="text" name="detailedAddress" defaultValue={user?.detailedAddress || ''} />
                            </div>
                            {user.userRole === "VET" && (
                                <div className="form-group">
                                    <label>수의사 이미지</label>
                                    {user.vetImage ? (
                                                <img
                                                    src={`${process.env.REACT_APP_API_BASE_URL}/images/${user.vetImage}`}
                                                    alt="수의사 프로필 이미지"
                                                    style={{ width: "120px", height: "120px", objectFit: "cover" }}
                                                />
                                            ) : (
                                                <p>이미지가 없습니다.</p>
                                            )}
                                    <input type="file" name='image' accept="image/*"/>
                                </div>
                            )}
                            <button type="submit">저장</button>
                            <button type="button" onClick={handleDeleteUser}>
                                삭제
                            </button>
                        </form>
                    </div>
                ) : (
                    <div className="pet-info">
                        <h3>{user.name}님의 반려동물</h3>
                        <div className="pet-list">
                            {pets.length === 0 ? (
                                <p>등록된 반려동물이 없습니다.</p>
                            ) : (
                                pets.map((pet) => (
                                    <div
                                        key={pet.petId}
                                        className="pet-card"
                                        onClick={() => handleViewPet(pet)}
                                    >
                                        <p>{pet.name}</p>
                                        <img
                                            src={
                                                pet.petImage && typeof pet.petImage === "string"
                                                    ? `${process.env.REACT_APP_API_BASE_URL}/${pet.petImage.replace(/\\/g, "/")}`
                                                    : "/images/default_pet.jpg" // 기본 이미지 경로
                                            }
                                            alt={pet.name}
                                            className="pet-photo"
                                        />
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                )}

                {isProfileVisible && (
                    <PetProfileModal
                        pet={selectedPet}
                        onEdit={handleEditPet}
                        onDelete={(petId) => handleDeletePet(petId)}
                        onClose={handleCloseProfile}
                    />
                )}
                {isEditVisible && (
                    <PetEditModal
                        pet={editingPet}
                        onSave={handleSavePet}
                        onClose={handleCloseEdit}
                    />
                )}
                {isRegistrationVisible && (
                    <PetRegistrationModal
                        onClose={() => setIsRegistrationVisible(false)}
                        onSave={handleAddNewPet}
                    />
                )}
            </div>
        </div>
    );
}

export default MyPage;