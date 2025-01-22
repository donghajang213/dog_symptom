import React from 'react';
import { Link } from 'react-router-dom';
import './ProductList.css';

function ProductList() {
  const productData = [
    { id: 1, name: 'CBD 릴리브 오일', price: '₩50,000', description: '발작 및 통증 완화', image: '/images/product1.jpg' },
    { id: 2, name: '펫비타민', price: '₩30,000', description: '반려동물 건강 필수 비타민', image: '/images/product2.jpg' },
  ];

  return (
    <div className="product-list-container">
      <h2>제품 보기</h2>
      <div className="product-list">
        {productData.map(product => (
          <div className="product-item" key={product.id}>
            <img src={product.image} alt={product.name} className="product-image" />
            <h3>{product.name}</h3>
            <p>{product.price}</p>
            <Link to={`/product/${product.id}`} className="view-details-button">상세 보기</Link>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProductList;
