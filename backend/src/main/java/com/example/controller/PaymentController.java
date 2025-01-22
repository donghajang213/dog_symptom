package com.example.controller;

import com.example.entity.PaymentEntity;
import com.example.service.PaymentService;
import com.example.service.UserTokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserTokenService userTokenService;

    public PaymentController(PaymentService paymentService, UserTokenService userTokenService) {
        this.paymentService = paymentService;
        this.userTokenService = userTokenService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(@RequestBody Map<String, Object> request,
                                            HttpSession session) {
        try {
            // 세션에서 사용자 정보 가져오기
            String userId = (String) session.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보가 필요합니다.");
            }

            // 요청 데이터 디버깅
            System.out.println("Received Request: " + request);

            // 요청 데이터 파싱
            String orderId = (String) request.get("orderId");
            String orderName = (String) request.get("orderName");
            Integer amount = (Integer) request.get("amount");
            String successUrl = (String) request.get("successUrl");
            String failUrl = (String) request.get("failUrl");

            System.out.println("Parsed Data - OrderId: " + orderId + ", OrderName: " + orderName + ", Amount: " + amount);

            // PaymentEntity 생성 및 저장
            PaymentEntity payment = new PaymentEntity();
            payment.setOrderId(orderId);
            payment.setOrderName(orderName);
            payment.setAmount(amount);
            payment.setUserId(userId);
            payment.setStatus("PENDING"); // 결제 요청 상태
            payment.setCreatedAt(LocalDateTime.now());

            // 결제 요청 API 호출
            Map<String, Object> response = paymentService.requestPayment(payment, successUrl, failUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 요청 실패: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            // 요청 데이터 파싱
            String paymentKey = (String) request.get("paymentKey");
            String orderId = (String) request.get("orderId");
            String orderName = (String) request.get("orderName");
            Integer amount = Integer.parseInt(request.get("amount").toString());
            int purchasedTokens = extractTokensFromOrderName(orderName); // 구매 뼈다귀

            // 세션에서 사용자 정보 가져오기
            String userId = (String) session.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보가 필요합니다.");
            }

            // Toss Payments API 승인 요청
            Map<String, Object> response = paymentService.confirmPayment(paymentKey, orderId, orderName, amount);

            // 승인된 결제 정보를 DB에 저장
            paymentService.savePaymentInfo(paymentKey, orderId, orderName, amount, "SUCCESS", userId);

            // 토큰 업데이트
            userTokenService.updateTokens(userId, purchasedTokens);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();

            // 실패 처리
            try {
                String paymentKey = (String) request.get("paymentKey");
                String orderId = (String) request.get("orderId");
                String orderName = (String) request.get("orderName");
                int purchasedTokens = extractTokensFromOrderName(orderName);
                String userId = (String) session.getAttribute("userId");

                if (userId != null) {
                    // 토큰 업데이트 (실패 시에도 누적)
                    userTokenService.updateTokens(userId, purchasedTokens);

                    // 결제 정보 저장
                    paymentService.savePaymentInfo(paymentKey, orderId, orderName, Integer.parseInt(request.get("amount").toString()), "FAILED", userId);
                }
            } catch (Exception ex) {
                System.err.println("결제 실패 처리 중 오류: " + ex.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 승인 실패: " + e.getMessage());
        }
    }

    private int extractTokensFromOrderName(String orderName) {
        // orderName 값 로그 출력
        System.out.println("Extracting tokens from orderName: " + orderName);

        // 예: "뼈다귀 10개" -> 숫자 부분만 추출
        if (orderName != null && orderName.matches("뼈다귀 \\d+개")) {
            int tokens = Integer.parseInt(orderName.replaceAll("\\D", ""));
            System.out.println("Extracted Tokens: " + tokens);
            return tokens;
        }

        System.out.println("Failed to extract tokens. Returning 0.");
        return 0;
    }
}

