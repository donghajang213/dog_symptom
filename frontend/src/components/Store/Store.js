import React, { useState, useEffect } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import Slider from 'react-slick';
import './Store.css';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

function Store() {
  const location = useLocation();
  const isHome = location.pathname === '/store'; // 현재 경로가 스토어 홈인지 확인
  const [searchQuery, setSearchQuery] = useState(''); // 검색어 상태 관리
  const [banners, setBanners] = useState([]); // 배너 데이터 상태 관리

  // 슬라이더 설정
  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 3000,
    arrows: true, // 화살표 활성화
    prevArrow: <button type="button" className="slick-prev">←</button>, // 왼쪽 화살표 커스터마이징
    nextArrow: <button type="button" className="slick-next">→</button>, // 오른쪽 화살표 커스터마이징
  };


  // 현재 날짜
  const currentDate = new Date();

  // 배너 데이터 가져오기
  useEffect(() => {
    const fetchBanners = async () => {
      try {
        // 서버 API 호출
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/banners`);

        // 응답 확인
        if (!response.ok) {
          throw new Error(`서버 응답 오류: ${response.status} - ${response.statusText}`);
        }

        // JSON 데이터 파싱
        const data = await response.json();

        // 활성화된 배너 필터링
        const activeBanners = data
          .filter((banner) => {
            const startDate = new Date(banner.startDate);
            const endDate = new Date(banner.endDate);
            return currentDate >= startDate && currentDate <= endDate;
          })
          .sort((a, b) => a.priority - b.priority); // 우선순위 정렬

        setBanners(activeBanners);
      } catch (error) {
        console.error("배너 데이터를 가져오는 중 오류 발생:", error.message);
      }
    };

    fetchBanners();
  }, []); // 빈 의존성 배열로 한 번만 실행

  // 검색 처리
  const handleSearch = (e) => {
    e.preventDefault();
    alert(`검색어: ${searchQuery}`);
  };

  return (
    <div className="store-container">
      <header className="store-header">
        <div className="store-nav">
          <ul className="nav-links">
            <li><Link to="/store">스토어 홈</Link></li>
            <li><Link to="/store/products">전체 제품</Link></li>
            <li><Link to="/store/best">베스트</Link></li>
            <li><Link to="/store/event">이벤트</Link></li>
          </ul>
          <div className="store-actions">
            <form onSubmit={handleSearch} className="search-form">
              <input
                type="text"
                placeholder="검색어를 입력하세요"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="search-input"
              />
              <button type="submit" className="search-button">검색</button>
            </form>
            <Link to="/store/cart" className="cart-button">장바구니</Link>
          </div>
        </div>
      </header>

      {/* 배너 슬라이더 */}
      {isHome && banners.length > 0 && (
        <section className="banner-slider">
          <Slider {...sliderSettings}>
            {banners.map((banner) => (
              <div key={banner.bannerId}>
                <a href={banner.redirectUrl} target="_blank" rel="noopener noreferrer">
                  <img
                    src={banner.imageUrl} // 이미지 URL 수정
                    alt={`배너 ${banner.bannerId}`}
                    className="slider-image"
                  />
                </a>
              </div>
            ))}
          </Slider>
        </section>
      )}

      {/* 스토어 메인 콘텐츠 */}
      <section className="store-main">
        <Outlet />
      </section>
    </div>
  );
}

export default Store;
