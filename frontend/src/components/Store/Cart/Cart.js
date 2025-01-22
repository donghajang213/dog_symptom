import React from 'react';
import { Link } from 'react-router-dom';
import './Cart.css';

function Cart() {
  // 장바구니 상품 더미 데이터 (빈 배열로 테스트 가능)
  const cartItems = [
    // { id: 1, name: "상품 1", price: "10,000원", image: "/images/product1.jpg" },
    // { id: 2, name: "상품 2", price: "20,000원", image: "/images/product2.jpg" },
  ];

  return (
    <div className="cart-container">
      <h2>장바구니</h2>
      {cartItems.length === 0 ? (
        <div className="empty-cart">
          <img
            src="/images/empty_cart.png" // 빈 장바구니 이미지 경로
            alt="빈 장바구니"
            className="empty-cart-image"
          />
          <p className="empty-cart-text">장바구니에 담긴 제품이 없어요.</p>
          <Link to="/store" className="go-store-button">
            스토어로 이동
          </Link>
        </div>
      ) : (
        <div className="cart-items">
          {cartItems.map((item) => (
            <div key={item.id} className="cart-item">
              <img
                src={item.image}
                alt={item.name}
                className="cart-item-image"
              />
              <div className="cart-item-details">
                <h3>{item.name}</h3>
                <p>{item.price}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Cart;
