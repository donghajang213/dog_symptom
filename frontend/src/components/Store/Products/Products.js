import React, { useState, useEffect } from 'react';
import './Products.css';

function Products() {
  const [products, setProducts] = useState([]); // 제품 데이터를 관리하는 상태
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태

  // 백엔드에서 제품 데이터 가져오기
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/products`);
        if (!response.ok) {
          throw new Error('제품 데이터를 불러오는 데 실패했습니다.');
        }
        const data = await response.json();
        setProducts(data); // 데이터 설정
      } catch (error) {
        setError(error.message);
      } finally {
        setIsLoading(false); // 로딩 상태 해제
      }
    };

    fetchProducts();
  }, []);

  if (isLoading) {
    return <div className="loading">제품을 불러오는 중입니다...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="products-container">
      <h2>전체 제품</h2>
      <div className="products-grid">
        {/* 제품 데이터 렌더링 */}
        {products.map((product) => (
          <div key={product.prod_id} className="product-card">
            <img
              src={product.prod_main_img_path || '/images/default-product.jpg'} // 이미지 경로
              alt={product.prod_name}
              className="product-image"
            />
            <h3>{product.prod_name}</h3>
            <p>{product.prod_sale_price.toLocaleString()}원</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Products;
