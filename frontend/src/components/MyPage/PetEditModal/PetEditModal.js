import React, { useState, useEffect } from "react";
import "./PetEditModal.css";

function PetEditModal({ pet, onClose, onSave }) {
  const [formData, setFormData] = useState({
    name: "",
    breed: "",
    birthYear: "",
    birthMonth: "",
    birthDay: "",
    gender: "",
    neutered: "",
    weightKg: "",
    registrationNumber: "",
    photo: "",
  });

  // pet 데이터로 초기값 설정
  useEffect(() => {
    if (pet) {
      const birthDate = pet.birthDate ? pet.birthDate.split("-") : ["", "", ""];
      setFormData({
        petId: pet.petId,
        userId: pet.userId, // 기존 사용자 ID를 포함
        name: pet.name || "",
        breed: pet.breed || "",
        birthYear: birthDate[0],
        birthMonth: birthDate[1],
        birthDay: birthDate[2],
        gender: pet.gender || "",
        neutered: pet.neuteringStatus || "",
        weightKg: pet.weightKg || "",
        registrationNumber: pet.registrationNumber || "",
        photo: pet.petImage || "", // 기존 이미지 경로
      });
    }
  }, [pet]);

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "photo" && files && files.length > 0) {
      setFormData({ ...formData, photo: files[0] }); // 새로 업로드된 파일
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 생일 데이터 합치기
    const birthDate = `${formData.birthYear}-${formData.birthMonth.padStart(2, "0")}-${formData.birthDay.padStart(2, "0")}`;

    // FormData 생성
    const formDataToSend = new FormData();
    formDataToSend.append("petId", pet.petId);
    formDataToSend.append("name", formData.name);
    formDataToSend.append("breed", formData.breed);
    formDataToSend.append("birthDate", birthDate);
    formDataToSend.append("gender", formData.gender);
    formDataToSend.append("neuteringStatus", formData.neutered);
    formDataToSend.append("weightKg", formData.weightKg);
    formDataToSend.append("registrationNumber", formData.registrationNumber);
    if (formData.photo && typeof formData.photo !== "string") {
      formDataToSend.append("photo", formData.photo);
    }

    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/pets/edit`, {
        method: "PUT",
        credentials: "include",
        body: formDataToSend,
      });

      if (response.ok) {
        const updatedPet = await response.json(); // 백엔드에서 수정된 데이터 반환
        onSave(updatedPet); // 부모 컴포넌트(MyPage)로 수정된 데이터 전달
        window.location.reload(); // 페이지 전체 새로고침
        onClose(); // 모달 닫기
      } else {
        const errorText = await response.text();
        alert("수정 실패: " + errorText);
      }
    } catch (error) {
      console.error("수정 요청 중 오류 발생:", error);
      alert("네트워크 오류가 발생했습니다.");
    }
  };


  return (
      <div className="modal-overlay">
        <div className="modal-container">
          <h2 className="modal-title">프로필 수정</h2>
          <form onSubmit={handleSubmit} className="modal-form">
            {/* 이미지 업로드 */}
            <div className="photo-upload">
              <label htmlFor="photo-input" className="photo-label">
                {formData.photo ? (
                    <img
                        src={
                          typeof formData.photo === "string"
                              ? `${process.env.REACT_APP_API_BASE_URL}/${formData.photo.replace(/\\/g, "/")}`
                              : URL.createObjectURL(formData.photo)
                        }
                        alt="반려동물"
                        className="preview-photo"
                    />
                ) : (
                    <div className="photo-placeholder">+</div>
                )}
              </label>
              <input
                  type="file"
                  id="photo-input"
                  name="photo"
                  accept="image/*"
                  onChange={handleChange}
                  className="photo-input"
              />
            </div>

            {/* 이름 */}
            <label className="form-label">
              이름
              <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="반려동물 이름"
                  required
              />
            </label>

            {/* 품종 */}
            <label className="form-label">
              품종
              <select
                  name="breed"
                  value={formData.breed}
                  onChange={handleChange}
                  required
              >
                <option value="">품종을 선택하세요</option>
                <option value="믹스">믹스</option>
                <option value="말티즈">말티즈</option>
                <option value="푸들">푸들</option>
                <option value="시바견">시바견</option>
              </select>
            </label>

            {/* 생일 */}
            <label className="form-label">
              생일
              <div className="birth-inputs">
                <input
                    type="number"
                    name="birthYear"
                    value={formData.birthYear}
                    onChange={handleChange}
                    placeholder="YYYY"
                    required
                />
                <input
                    type="number"
                    name="birthMonth"
                    value={formData.birthMonth}
                    onChange={handleChange}
                    placeholder="MM"
                    required
                />
                <input
                    type="number"
                    name="birthDay"
                    value={formData.birthDay}
                    onChange={handleChange}
                    placeholder="DD"
                    required
                />
              </div>
            </label>

            {/* 성별 */}
            <label className="form-label">
              성별
              <div className="gender-options">
                <input
                  type="radio"
                  id="male"
                  name="gender"
                  value="남자아이"
                  checked={formData.gender === "남자아이"}
                  onChange={handleChange}
                />
                <label htmlFor="male">남자아이</label>

                <input
                  type="radio"
                  id="female"
                  name="gender"
                  value="여자아이"
                  checked={formData.gender === "여자아이"}
                  onChange={handleChange}
                />
                <label htmlFor="female">여자아이</label>
              </div>
            </label>

            {/* 중성화 여부 */}
            <label className="form-label">
              중성화 여부
              <div className="neutered-options">
                <input
                  type="radio"
                  id="neutered-before"
                  name="neutered"
                  value="중성화 전"
                  checked={formData.neutered === "중성화 전"}
                  onChange={handleChange}
                />
                <label htmlFor="neutered-before">중성화 전</label>

                <input
                  type="radio"
                  id="neutered-done"
                  name="neutered"
                  value="중성화 완료"
                  checked={formData.neutered === "중성화 완료"}
                  onChange={handleChange}
                />
                <label htmlFor="neutered-done">중성화 완료</label>
              </div>
            </label>

            {/* 체중 */}
            <label className="form-label">
              체중 (kg)
              <input
                  type="number"
                  name="weightKg"
                  value={formData.weightKg}
                  onChange={handleChange}
                  placeholder="체중 입력"
                  step="0.1"
              />
            </label>

            <div className="form-actions">
              <button type="button" className="cancel-button" onClick={onClose}>
                취소
              </button>
              <button type="submit" className="save-button">
                저장
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}

export default PetEditModal;