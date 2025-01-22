import React, { useState, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { UserProvider, useUser } from './contexts/UserContext'; // 전역 사용자 상태 관리
import { DashboardProvider } from './contexts/DashboardContext'; // 대시보드 데이터 관리
import { BrowserRouter as Router } from "react-router-dom";
import { CheckoutPage } from "./components/AdminPage/Payments/Checkout"; // 토스 결제 페이지
import { SuccessPage } from './components/AdminPage/Payments/Sucess'; // 결제 승인 전 페이지
import { FailPage } from './components/AdminPage/Payments/Fail'; // 결제 실패 페이지
import { PaymentSuccessPage } from "./components/AdminPage/Payments/PaymentSuccessPage"; // 결제 완료 페이지
import { PayPage } from "./components/AdminPage/Payments/PayPage"; // 뼈다귀 선택 페이지
import { ChatRoom } from "./components/Chat/ChatRoom"; // 메인 채팅방
import MainPage from './components/MainPage/MainPage'; // 메인 페이지
import Header from './components/Header/Header'; // 공통 헤더
import Login from './components/Login/Login'; // 로그인 페이지
import Clinic from './components/Clinic/Clinic'; // 클리닉 메인 페이지
import Hospital from './components/Hospital/Hospital'; // 근처 병원 페이지
import VetSelectWithGps from './components/Clinic/VetSelect/VetSelectWithGps'; // 상담사 선택 페이지
import GPTConsult from './components/Clinic/GPTConsult/GPTConsult'; // GPT 상담 페이지
import VetConsult from './components/Clinic/VetConsult/VetConsult'; // 수의사 상담 페이지
import PhotoUpload from './components/Clinic/PhotoUpload'; // 사진 업로드 페이지
import WebSocketService from './services/WebSocketService';
import PetSelect from './components/Clinic/PetSelect';
import ChatApp from "./components/Clinic/GPTConsult/ChatApp";
import VetConsultRequest from "./components/Clinic/ConsultRequest/VetConsultRequest"; // 상담 내역 페이지
import PostForm from "./components/Board/PostForm"; // 게시판 작성 폼
import PostList from "./components/Board/PostList"; // 게시판 리스트
import BoardDetail from "./components/Board/PostDetail"; // 게시판 상세보기
import EditBoard from "./components/Board/EditBoard"; // 마이페이지
import MyPage from './components/MyPage/MyPage'; // 마이페이지

import './App.css'; // 스타일링

function App() {
    const [messages, setMessages] = useState([]); // State to track WebSocket messages
    const { user } = useUser() || {}; // Get user info from context

    useEffect(() => {
        if (user) {
            const { chatId } = user;
            if (chatId) {
                WebSocketService.connect(chatId, (data) => {
                    console.log("WebSocket Message Received:", data);
                    setMessages((prevMessages) => [...prevMessages, data]);
                });
            }
        }

        return () => {
            WebSocketService.disconnect();
        };
    }, [user]);

    return (
      <UserProvider> {/* UserProvider로 전역 상태를 감쌈 */}
        <DashboardProvider> {/* DashboardProvider로 관리자 페이지 관련 데이터를 감쌈 */}
          <div className="App">
            <Header /> {/* 모든 페이지에서 공통으로 사용되는 헤더 */}
            <Routes>
              {/* 메인 관련 라우팅 */}
              <Route path="/" element={<MainPage />} /> {/* 메인 페이지 */}
              <Route path="/login" element={<Login />} /> {/* 로그인 페이지 */}
              <Route path="/clinic" element={<Clinic />} /> {/* 클리닉 메인 페이지 */}
              <Route path="/clinic/vetselect" element={<VetSelectWithGpsWrapper />} /> {/* 수의사 선택 페이지 */}
              <Route path="/hospital" element={<Hospital />} /> {/* 병원 메인 페이지 */}
              <Route path="/clinic/gpt" element={<GPTConsult />} /> {/* GPT 상담 페이지 */}
              <Route path="/clinic/vet" element={<VetConsult />} /> {/* 수의사 상담 페이지 */}
              <Route path="/mypage" element={<MyPage />} /> {/* 마이페이지 */}
              <Route path="/chat" element={<ChatRoom />} /> {/* 채팅방 */}
              <Route path="/clinic/photo" element={<PhotoUpload />} /> {/* 사진 업로드 페이지 추가 */}
              <Route path="/clinic/select-pet" element={<PetSelect />} /> {/* 펫 선택 페이지 */}
              <Route path="/clinic/chat" element={<ChatApp />} /> {/* GPT모델 */}
              <Route path="/board/form" element={<PostForm />} /> {/* 보드 작성 폼 */}
              <Route path="/board/list" element={<PostList />} /> {/* 보드 리스트 */}
              <Route path="/board/detail/:boardId" element={<BoardDetail />} /> {/*보드 상세보기 */}
              <Route path="/board/edit/:boardId" element={<EditBoard />} /> {/*보드 수정페이지 */}
              <Route path="/clinic/request" element={<VetConsultRequest />} /> {/* 상담 요청 페이지 */}

            {/* 결제 관련 라우팅 */}
            <Route path="/payment" element={<CheckoutPage />} /> {/* 결제 페이지 */}
            <Route path="/sandbox/success" element={<SuccessPage />} />
            <Route path="/sandbox/fail" element={<FailPage />} />
            <Route path="/payment-success" element={<PaymentSuccessPage />} />
            <Route path="/paypage" element={<PayPage />} />

            {/* 회원가입 관련 라우팅 */}
            <Route path="/signup" element={<SignupSelection />} /> {/* 회원가입 선택 페이지 */}
            <Route path="/signup/general" element={<SignupGeneral />} /> {/* 일반 회원가입 페이지 */}
            <Route path="/signup/vet" element={<SignupVet />} /> {/* 수의사 회원가입 페이지 */}
            <Route path="/signup/seller" element={<SignupSeller />} /> {/* 판매자 회원가입 페이지 */}

            {/* 스토어 관련 라우팅 */}
            <Route path="/store" element={<Store />}>
              <Route path="products" element={<Products />} /> {/* 전체 제품 페이지 */}
              <Route path="best" element={<Best />} /> {/* 베스트 제품 페이지 */}
              <Route path="event" element={<Event />} /> {/* 이벤트 페이지 */}
              <Route path="cart" element={<Cart />} /> {/* 장바구니 페이지 */}
            </Route>

            {/* 관리자 페이지 */}
                <Route path="/admin/*" element={<ProtectedRoute element={<AdminPage />} />}>
                    <Route path="dashboard" element={<Dashboard />} />
                    <Route path="dashboard/ranking/vet" element={<VetRanking />} />
                    <Route path="dashboard/ranking/disease" element={<DiseaseRanking />} />
                    <Route path="dashboard/ranking/breed" element={<BreedRanking />} />
                    <Route path="dashboard/ranking/signup" element={<SignupRanking />} />
                    <Route path="dashboard/ranking/product" element={<ProductRanking />} />
                    <Route path="dashboard/ranking/origin" element={<OriginRanking />} />
                </Route>
            </Routes>
          </div>
      </DashboardProvider>
    </UserProvider>
  );
}

const VetSelectWithGpsWrapper = () => {
  const { user } = useUser();

  if (!user) {
    return <p>로그인이 필요합니다.</p>;
  }

  return <VetSelectWithGps userId={user.userId} />;
};

export default App;
