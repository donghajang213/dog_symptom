import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./PetRegistrationModal.css";

function PetRegistrationModal({ onClose, pet }) {
  const [formData, setFormData] = useState(
      pet || {
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
      }
  );
  const navigate = useNavigate(); // 페이지 이동에 사용

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "photo") {
      setFormData({ ...formData, photo: files[0] });
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 생일을 yyyy-MM-dd 형식으로 변환
    const birthDate = `${formData.birthYear}-${formData.birthMonth.padStart(2, "0")}-${formData.birthDay.padStart(2, "0")}`;

    const payload = new FormData();
    payload.append("name", formData.name);
    payload.append("breed", formData.breed);
    payload.append("birthDate", birthDate);
    payload.append("gender", formData.gender);
    payload.append("neuteringStatus", formData.neutered);
    payload.append("weightKg", formData.weightKg);
    payload.append("registrationNumber", formData.registrationNumber);
    if (formData.photo) {
      payload.append("photo", formData.photo);
    }

    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/pets/register`, {
        method: "POST",
        body: payload,
        credentials: "include",
      });

      if (response.ok) {
        console.log("반려동물 등록 성공");
        alert("반려동물이 등록되었습니다.");

        // 모달 닫기 (옵션)
        onClose?.();

        // MyPage로 이동
        navigate("/mypage");
      } else {
        const errorText = await response.text();
        console.error("서버 오류:", errorText);
        alert("등록 실패: " + errorText);
      }
    } catch (error) {
      console.error("오류 발생:", error);
      alert("네트워크 오류가 발생했습니다.");
    }
  };


  return (
      <div className="modal-overlay">
        <div className="modal-container">
          <h2 className="modal-title">{pet ? "프로필 수정" : "프로필 등록"}</h2>
          <form onSubmit={handleSubmit} className="modal-form">
            {/* 반려동물 사진 업로드 */}
            <div className="photo-upload">
              <label htmlFor="photo-input" className="photo-label">
                {formData.photo ? (
                    <img
                        src={URL.createObjectURL(formData.photo)}
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
                  className="form-input"
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
                  className="form-input"
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
                    className="form-input birth-input"
                    min="1900"
                    max={new Date().getFullYear()}
                    required
                />
                <input
                    type="number"
                    name="birthMonth"
                    value={formData.birthMonth}
                    onChange={handleChange}
                    placeholder="MM"
                    className="form-input birth-input"
                    min="1"
                    max="12"
                    required
                />
                <input
                    type="number"
                    name="birthDay"
                    value={formData.birthDay}
                    onChange={handleChange}
                    placeholder="DD"
                    className="form-input birth-input"
                    min="1"
                    max="31"
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
              <div className="neuter-options">
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
                  className="form-input"
                  min="0"
                  step="0.1"
              />
            </label>

            {/* 저장 버튼 */}
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

export default PetRegistrationModal;