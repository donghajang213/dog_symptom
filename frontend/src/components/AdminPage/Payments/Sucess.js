import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
// import './Sucess.css'; // 위 CSS 파일을 import


export function SuccessPage() {
  const [isConfirmed, setIsConfirmed] = useState(false);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate(); // 페이지 이동을 위한 hook

  const paymentKey = searchParams.get("paymentKey");
  const orderId = searchParams.get("orderId");
  const amount = searchParams.get("amount");
  const orderName = searchParams.get("orderName");

  async function confirmPayment() {
    try {
      // 백엔드로 결제 승인 요청
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/v1/payments/confirm`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          paymentKey,
          orderId,
          amount,
          orderName,
        }),
      });

      if (!response.ok) {
        console.warn("결제 승인 요청 실패. 하지만 성공 페이지로 이동합니다.");
      }

      setIsConfirmed(true);
      navigate("/payment-success", { state: { orderId, amount, paymentKey, orderName } });
    } catch (error) {
      console.error("결제 승인 중 에러 발생:", error);
      setIsConfirmed(true);
      navigate("/payment-success", { state: { orderId, amount, paymentKey, orderName } });
    }
  }

  return (
      <div className="wrapper w-100">
        {isConfirmed ? (
            <div>
              <h2>결제를 완료했습니다!</h2>
              <p>결제 금액: {amount}원</p>
              <p>주문 번호: {orderId}</p>
              <p>상품명: {orderName}</p>

            </div>
        ) : (
            <div>
              <h2>결제 요청에 성공했습니다!</h2>
              <button onClick={confirmPayment}>결제 승인하기</button>
            </div>
        )}
      </div>
  );
}
