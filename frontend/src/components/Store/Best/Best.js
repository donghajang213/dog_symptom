import React, { useState, useEffect } from 'react';
import './Best.css';

function Best() {
  const [bestProducts, setBestProducts] = useState([]); // 베스트 제품 데이터를 관리하는 상태
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태

  // 백엔드에서 베스트 제품 데이터 가져오기
  useEffect(() => {
    const fetchBestProducts = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/products/best`);
        if (!response.ok) {
          throw new Error('베스트 제품 데이터를 불러오는 데 실패했습니다.');
        }
        const data = await response.json();
        setBestProducts(data); // 데이터 설정
      } catch (error) {
        setError(error.message);
      } finally {
        setIsLoading(false); // 로딩 상태 해제
      }
    };

    fetchBestProducts();
  }, []);

  if (isLoading) {
    return <div className="loading">베스트 제품을 불러오는 중입니다...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="best-container">
      <h2>베스트 제품</h2>
      <div className="best-grid">
        {/* 베스트 제품 데이터 렌더링 */}
        {bestProducts.map((product) => (
          <div key={product.prod_id} className="best-card">
            <img
              src={product.prod_main_img_path || '/images/default-product.jpg'} // 이미지 경로
              alt={product.prod_name}
              className="best-image"
            />
            <h3>{product.prod_name}</h3>
            <p>{product.sales}개 판매</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Best;
