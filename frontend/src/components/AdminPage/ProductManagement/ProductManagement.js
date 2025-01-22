import React, { useState } from 'react';
import ProductRegistration from './ProductRegistration/ProductRegistration'; // 상품 등록 컴포넌트
import ProductDetails from './ProductDetails/ProductDetails'; // 상품 상세 정보 보기 컴포넌트
import ProductEditDelete from './ProductEditDelete/ProductEditDelete'; // 상품 수정 및 삭제 컴포넌트
import './ProductManagement.css';

function ProductManagement() {
  const [activeSubMenu, setActiveSubMenu] = useState('register'); // 현재 활성화된 하위 메뉴

  return (
    <div className="product-management">
      {/* 사이드바 */}
      <aside className="sidebar">
        <h3>상품 관리</h3>
        <ul>
          <li
            className={activeSubMenu === 'register' ? 'active' : ''}
            onClick={() => setActiveSubMenu('register')}
          >
            상품 등록
          </li>
          <li
            className={activeSubMenu === 'details' ? 'active' : ''}
            onClick={() => setActiveSubMenu('details')}
          >
            상품 상세 정보 보기
          </li>
          <li
            className={activeSubMenu === 'edit-delete' ? 'active' : ''}
            onClick={() => setActiveSubMenu('edit-delete')}
          >
            상품 삭제
          </li>
        </ul>
      </aside>

      {/* 메인 콘텐츠 영역 */}
      <main className="content">
        {activeSubMenu === 'register' && <ProductRegistration />}
        {activeSubMenu === 'details' && <ProductDetails />}
        {activeSubMenu === 'edit-delete' && <ProductEditDelete />}
      </main>
    </div>
  );
}

export default ProductManagement;
