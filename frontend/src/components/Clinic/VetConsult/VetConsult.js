import React, { useState } from 'react';
import './VetConsult.css';

function VetConsult() {
  const [formData, setFormData] = useState({
    petName: '',
    symptoms: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.petName || !formData.symptoms) {
      alert('반려동물 이름과 증상을 입력해주세요.');
      return;
    }

    try {
      // 여기에 실제 API 호출 로직 추가 가능
      // 예제에서는 가상 응답 처리
      alert('수의사 상담 요청이 성공적으로 접수되었습니다.');
      console.log('Submitted Data:', formData);
    } catch (error) {
      alert('상담 요청 중 오류가 발생했습니다.');
      console.error('Error during vet consult:', error);
    }
  };

  return (
    <div className="vet-consult-container">
      <h2>수의사 상담</h2>
      <p>수의사와 직접 상담하여 반려동물의 건강 상태를 확인하세요.</p>
      <form onSubmit={handleSubmit} className="vet-consult-form">
        <label>
          반려동물 이름:
          <input
            type="text"
            name="petName"
            value={formData.petName}
            onChange={handleChange}
            className="form-input"
            required
          />
        </label>
        <label>
          증상 설명:
          <textarea
            name="symptoms"
            value={formData.symptoms}
            onChange={handleChange}
            className="form-input"
            required
          ></textarea>
        </label>
        <button type="submit" className="consult-submit">
          상담 요청
        </button>
      </form>
    </div>
  );
}

export default VetConsult;
