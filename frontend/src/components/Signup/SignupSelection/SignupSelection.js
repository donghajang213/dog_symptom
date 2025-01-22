import React from 'react';
import { Link } from 'react-router-dom';
import './SignupSelection.css'; // 스타일 파일 연결

function SignupSelection() {
  return (
    <div className="signup-selection-container">
      <h2>회원가입</h2>
      <p>가입 방식을 선택해주세요.</p>
      <div className="signup-options">
        {/* 일반 가입 카드 */}
        <div className="signup-card">
          <img src="/images/GeneralIcon.png" alt="일반 회원" />
          <h3>일반 가입</h3>
          <p>기본 정보를 입력하고 일반 회원으로 가입하세요.</p>
          <Link to="/signup/general" className="signup-button">일반가입</Link>
        </div>

        {/* 수의사 가입 카드 */}
        <div className="signup-card">
          <img src="/images/VetIcon.png" alt="수의사 회원" />
          <h3>수의사 가입</h3>
          <p>수의사 인증번호와 정보를 입력하여 가입하세요.</p>
          <Link to="/signup/vet" className="signup-button">수의사가입</Link>
        </div>

        {/* 판매자 가입 카드 */}
        <div className="signup-card">
          <img src="/images/SellerIcon.png" alt="판매자 회원" />
          <h3>판매자 가입</h3>
          <p>사업자 정보를 입력하고 판매자로 가입하세요.</p>
          <Link to="/signup/seller" className="signup-button">판매자가입</Link>
        </div>
      </div>
    </div>
  );
}

export default SignupSelection;
