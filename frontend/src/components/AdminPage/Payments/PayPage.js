import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../../contexts/UserContext'; // UserContext 가져오기
// import './PayPage.css';

export function PayPage() {
  const [selectedOption, setSelectedOption] = useState(null);
  const navigate = useNavigate();
  const { user } = useUser(); // UserContext에서 사용자 정보 가져오기

  useEffect(() => {
    // 사용자 인증 상태 확인
    if (user === null) {
      navigate('/login', { state: { alertMessage: '로그인이 필요합니다.' } }); // 로그인 페이지로 리다이렉트하면서 메시지 전달
    }
  }, [user, navigate]);

  const options = [
    { id: 1, name: '뼈다귀 10개', price: 1900, emoji: '🦴' },
    { id: 2, name: '뼈다귀 30개', price: 5900, emoji: '🦴🦴🦴' },
    { id: 3, name: '뼈다귀 50개', price: 9900, emoji: '🦴🦴🦴🦴🦴' },
  ];

  const handleProceedToCheckout = () => {
    if (!selectedOption) {
      alert('상품을 선택해주세요!');
      return;
    }

    navigate('/payment', { state: { selectedOption } });
  };

  return (
      <div className="page-container">
        <h1>상품 선택</h1>
        <ul className="options-list">
          {options.map((option) => (
              <li key={option.id} onClick={() => setSelectedOption(option)}>
                <label>
                  <span className="emoji">{option.emoji}</span>
                  <span>
                {option.name} - {option.price.toLocaleString()}원
              </span>
                  <input
                      type="radio"
                      name="product"
                      value={option.id}
                      checked={selectedOption?.id === option.id}
                      onChange={() => setSelectedOption(option)}
                  />
                </label>
              </li>
          ))}
        </ul>
        <button className="checkout-button" onClick={handleProceedToCheckout}>
          결제 진행
        </button>
      </div>
  );
}
