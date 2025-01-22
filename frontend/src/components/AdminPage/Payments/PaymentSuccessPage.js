import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";
// import "./PaymentSuccessPage.css"; // CSS 파일 import

export function PaymentSuccessPage() {
    const location = useLocation();
    const navigate = useNavigate();

    // `state`에서 결제 정보 추출
    const { paymentKey, orderId, amount, orderName } = location.state || {};

    useEffect(() => {
        // `state`가 없는 경우 (직접 접근 방지)
        if (!paymentKey || !orderId || !amount || !orderName) {
            alert("결제 정보가 없습니다.");
            navigate("/"); // 메인 페이지로 리다이렉트
        }
    }, [paymentKey, orderId, amount, orderName, navigate]);

    if (!paymentKey || !orderId || !amount || !orderName) {
        return <p className="loading-message">결제 정보를 불러오는 중입니다...</p>;
    }

    return (
        <div className="payment-success-container">
            <div className="success-box">
                <h1>결제가 완료되었습니다!</h1>
                <p className="success-info">결제 금액: <span>{amount}원</span></p>
                <p className="success-info">주문 번호: <span>{orderId}</span></p>
                <p className="success-info">상품명: <span>{orderName}</span></p>
                <p className="success-info">결제 키: <span>{paymentKey}</span></p>
                <button className="go-home-button" onClick={() => navigate("/")}>
                    메인 페이지로 이동
                </button>
            </div>
        </div>
    );
}
