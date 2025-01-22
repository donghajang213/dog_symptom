import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { loadTossPayments, ANONYMOUS } from "@tosspayments/tosspayments-sdk";

const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";

export function CheckoutPage() {
  const location = useLocation();
  const { selectedOption } = location.state || {}; // PayPage에서 전달된 데이터
  const [widgets, setWidgets] = useState(null);

  // 전달된 옵션 데이터를 기반으로 금액 설정
  const [amount, setAmount] = useState({
    currency: "KRW",
    value: selectedOption?.price || 0,
  });

  useEffect(() => {
    async function fetchPaymentWidgets() {
      const tossPayments = await loadTossPayments(clientKey);
      const widgets = tossPayments.widgets({ customerKey: ANONYMOUS });
      setWidgets(widgets);
    }

    fetchPaymentWidgets();
  }, []);

  useEffect(() => {
    async function renderPaymentWidgets() {
      if (widgets == null) {
        return;
      }

      // 결제 금액을 위젯에 설정
      await widgets.setAmount(amount);

      await Promise.all([
        widgets.renderPaymentMethods({
          selector: "#payment-method",
          variantKey: "DEFAULT",
        }),
        widgets.renderAgreement({
          selector: "#agreement",
          variantKey: "AGREEMENT",
        }),
      ]);
    }

    renderPaymentWidgets();
  }, [widgets, amount]);

  return (
      <div className="wrapper w-100">
        <div className="max-w-540 w-100">
          <h1>결제 진행</h1>
          <p>선택한 상품: {selectedOption?.name}</p>
          <p>결제 금액: {selectedOption?.price.toLocaleString()}원</p>
          <div id="payment-method" className="w-100" />
          <div id="agreement" className="w-100" />
          <div className="btn-wrapper w-100">
            <button
                className="btn primary w-100"
                onClick={async () => {
                  try {
                    const orderId = generateRandomString();
                    const orderName = selectedOption?.name || "상품명 없음";

                    // 결제 요청
                    await widgets?.requestPayment({
                      orderId,
                      orderName,
                      customerName: "김토스",
                      customerEmail: "customer123@gmail.com",
                      successUrl:
                          `${window.location.origin}/sandbox/success?orderName=${encodeURIComponent(orderName)}&amount=${selectedOption?.price}&orderId=${orderId}`,
                      failUrl:
                          `${window.location.origin}/sandbox/fail`,
                    });
                  } catch (error) {
                    console.error("결제 요청 중 에러 발생:", error);
                    alert("결제 요청 중 문제가 발생했습니다.");
                  }
                }}
            >
              결제하기
            </button>
          </div>
        </div>
      </div>
  );
}
