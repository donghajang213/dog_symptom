import React from 'react';
import { useParams } from 'react-router-dom';
import './ProductDetails.css';

function ProductDetails() {
  const { productId } = useParams();
  const productData = [
    { id: 1, name: 'CBD 릴리브 오일', price: '₩50,000', description: '발작 및 통증 완화', image: '/images/product1.jpg' },
    { id: 2, name: '펫비타민', price: '₩30,000', description: '반려동물 건강 필수 비타민', image: '/images/product2.jpg' },
  ];

  const product = productData.find(item => item.id === parseInt(productId));

  return (
    <div className="product-details-container">
      <img src={product.image} alt={product.name} className="product-details-image" />
      <h2>{product.name}</h2>
      <p>{product.price}</p>
      <p>{product.description}</p>
    </div>
  );
}

export default ProductDetails;
