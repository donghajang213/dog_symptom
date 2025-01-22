import React from "react";
import "./PetProfileModal.css";

function PetProfileModal({ pet, onClose, onEdit, onDelete }) {
  const handleDelete = () => {
    if (window.confirm(`${pet.name}을(를) 삭제하시겠습니까?`)) {
      onDelete(pet.petId); // 부모 컴포넌트로 petId 전달
    }
  };

  return (
      <div className="modal-overlay">
        <div className="modal-container">
          <h2 className="modal-title">{pet.name}의 상세 정보</h2>

          {/* 반려동물 사진 */}
          <div className="photo-container">
            <img
                src={
                  pet.petImage
                      ? `${process.env.REACT_APP_API_BASE_URL}/${pet.petImage.replace(/\\/g, "/")}`
                      : "/images/default_pet.jpg" // 기본 이미지 경로
                }
                alt={pet.name}
                className="detail-photo"
            />
          </div>

          {/* 반려동물 정보 */}
          <div className="pet-info">
            <p>
              <strong>이름:</strong> {pet.name}
            </p>
            <p>
              <strong>품종:</strong> {pet.breed || "미등록"}
            </p>
            <p>
              <strong>생일:</strong> {pet.birthDate || "미등록"}
            </p>
            <p>
              <strong>성별:</strong> {pet.gender || "미등록"}
            </p>
            <p>
              <strong>중성화 여부:</strong> {pet.neuteringStatus || "미등록"}
            </p>
            <p>
              <strong>체중:</strong> {pet.weightKg ? `${pet.weightKg} kg` : "미등록"}
            </p>
          </div>

          {/* 액션 버튼 */}
          <div className="action-buttons">
            <button className="edit-button" onClick={() => onEdit(pet)}>
              수정
            </button>
            <button className="delete-button" onClick={handleDelete}>
              삭제
            </button>
            <button className="close-button" onClick={onClose}>
              닫기
            </button>
          </div>
        </div>
      </div>
  );
}

export default PetProfileModal;