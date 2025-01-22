import React, { useState } from 'react';
import { Routes, Route, Link } from 'react-router-dom';

// 회원 정보 및 권한 관리
import UserInfo from './UserInfo/UserInfo'; // UserInfo 컴포넌트 추가
import PermissionManagement from './PermissionManagement/PermissionManagement';

// 상품 관리
import ProductAdd from './ProductManagement/ProductAdd/ProductAdd';
import ProductDetails from './ProductManagement/ProductDetails/ProductDetails';
import ProductEditDelete from './ProductManagement/ProductEditDelete/ProductEditDelete';

// 결제 및 상담 관리
import Payments from './Payments/Payments';
import ChatInfo from './ChatInfo/ChatInfo';

// 대시보드 및 하위 컴포넌트
import Dashboard from './Dashboard/Dashboard';
import DiseaseRanking from './Dashboard/DiseaseRanking/DiseaseRanking';
import VetRanking from './Dashboard/VetRanking/VetRanking';
import BreedRanking from './Dashboard/BreedRanking/BreedRanking';
import SignupRanking from './Dashboard/SignupRanking/SignupRanking';
import ProductRanking from './Dashboard/ProductRanking/ProductRanking';
import OriginRanking from './Dashboard/OriginRanking/OriginRanking';

// 콘텐츠 관리 하위 컴포넌트
import ContentManagement from './ContentManagement/ContentManagement';
import EventManagement from './ContentManagement/EventManagement/EventManagement';
import BannerManagement from './ContentManagement/BannerManagement/BannerManagement';

import './AdminPage.css';

function AdminPage() {
  const [activeMenu, setActiveMenu] = useState(null); // 열려있는 메뉴 상태

  // 메뉴 열림/닫힘 토글 함수
  const toggleMenu = (menu) => {
    setActiveMenu((prevMenu) => (prevMenu === menu ? null : menu)); // 같은 메뉴 클릭 시 닫기
  };

  return (
    <div className="admin-page">
      {/* 좌측 네비게이션 바 */}
      <nav className="admin-sidebar">
        <ul>
          {/* 대시보드 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('dashboard')} className="menu-title">
              대시보드
            </div>
            {activeMenu === 'dashboard' && (
              <ul className="submenu">
                <li>
                  <Link to="dashboard">대시보드 메인</Link>
                </li>
                <li>
                  <Link to="dashboard/vet">수의사 랭킹</Link>
                </li>
                <li>
                  <Link to="dashboard/disease">질병 랭킹</Link>
                </li>
                <li>
                  <Link to="dashboard/breed">품종 랭킹</Link>
                </li>
                <li>
                  <Link to="dashboard/signup">회원가입 랭킹</Link>
                </li>
                <li>
                  <Link to="dashboard/product">상품 랭킹</Link>
                </li>
                <li>
                  <Link to="dashboard/origin">제조사 랭킹</Link>
                </li>
              </ul>
            )}
          </li>

          {/* 회원정보 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('userInfo')} className="menu-title">
              사용자 정보
            </div>
            {activeMenu === 'userInfo' && (
              <ul className="submenu">
                <li>
                  <Link to="userinfo">사용자 정보 보기</Link> {/* 수정된 부분 */}
                </li>
              </ul>
            )}
          </li>

          {/* 권한 관리 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('permissionManagement')} className="menu-title">
              권한 관리
            </div>
            {activeMenu === 'permissionManagement' && (
              <ul className="submenu">
                <li>
                  <Link to="permissionmanagement">권한 설정</Link>
                </li>
              </ul>
            )}
          </li>

          {/* 상품 관리 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('productManagement')} className="menu-title">
              상품 관리
            </div>
            {activeMenu === 'productManagement' && (
              <ul className="submenu">
                <li>
                  <Link to="productmanagement/add">상품 등록</Link>
                </li>
                <li>
                  <Link to="productmanagement/detail">상품 상세 정보 보기</Link>
                </li>
                <li>
                  <Link to="productmanagement/editdelete">상품 수정 및 삭제</Link>
                </li>
              </ul>
            )}
          </li>

          {/* 콘텐츠 관리 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('contentManagement')} className="menu-title">
              콘텐츠 관리
            </div>
            {activeMenu === 'contentManagement' && (
              <ul className="submenu">
                <li>
                  <Link to="contentmanagement">콘텐츠 목록</Link>
                </li>
                <li>
                  <Link to="contentmanagement/event">이벤트 관리</Link>
                </li>
                <li>
                  <Link to="contentmanagement/banner">배너 관리</Link>
                </li>
              </ul>
            )}
          </li>

          {/* 결제 관리 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('payments')} className="menu-title">
              결제 관리
            </div>
            {activeMenu === 'payments' && (
              <ul className="submenu">
                <li>
                  <Link to="payments">결제 내역 보기</Link>
                </li>
              </ul>
            )}
          </li>

          {/* 상담내역 관리 */}
          <li className="menu-item">
            <div onClick={() => toggleMenu('chatInfo')} className="menu-title">
              상담내역 관리
            </div>
            {activeMenu === 'chatInfo' && (
              <ul className="submenu">
                <li>
                  <Link to="chatinfo">상담 내역 확인</Link>
                </li>
              </ul>
            )}
          </li>
        </ul>
      </nav>

      {/* 중앙 콘텐츠 영역 */}
      <div className="admin-content">
        <Routes>
          {/* 대시보드 관리 하위 라우트 */}
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="dashboard/disease" element={<DiseaseRanking />} />
          <Route path="dashboard/vet" element={<VetRanking />} />
          <Route path="dashboard/breed" element={<BreedRanking />} />
          <Route path="dashboard/signup" element={<SignupRanking />} />
          <Route path="dashboard/product" element={<ProductRanking />} />
          <Route path="dashboard/origin" element={<OriginRanking />} />

          {/* 회원 관리 하위 라우트 */}
          <Route path="userinfo" element={<UserInfo />} /> {/* 수정된 부분 */}

          {/* 권한 관리 하위 라우트 */}
          <Route path="permissionmanagement" element={<PermissionManagement />} />

          {/* 상품 관리 하위 라우트 */}
          <Route path="productmanagement/add" element={<ProductAdd />} />
          <Route path="productmanagement/detail" element={<ProductDetails />} />
          <Route path="productmanagement/editdelete" element={<ProductEditDelete />} />

          {/* 콘텐츠 관리 하위 라우트 */}
          <Route path="contentmanagement" element={<ContentManagement />} />
          <Route path="contentmanagement/event" element={<EventManagement />} />
          <Route path="contentmanagement/banner" element={<BannerManagement />} />

          {/* 결제 관리 하위 라우트 */}
          <Route path="payments" element={<Payments />} />

          {/* 상담내역 관리 하위 라우트 */}
          <Route path="chatinfo" element={<ChatInfo />} />
        </Routes>
      </div>
    </div>
  );
}

export default AdminPage;
