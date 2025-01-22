import React from 'react';
import './MainPage.css'; // 스타일 파일 가져오기

function MainPage() {
  return (
    <div className="main-page">
      <div className="background">
        <video autoPlay muted loop className="background-video">
          <source src="/assets/pet_video.mp4" type="video/mp4" />
          Your browser does not support the video tag.
        </video>
      </div>
      <div className="main-content">
        <h1 className="main-title">아토피아에 오신 것을 환영합니다</h1>
        <p className="main-description">
          AI 기술을 활용해 반려동물의 건강을 확인하고, 더 나은 관리 방법을 제안합니다.
        </p>
        <div className="button-group">
          <button className="app-store-btn">App Store</button>
          <button className="google-play-btn">Google Play</button>
        </div>
      </div>
    </div>
  );
}

export default MainPage;
