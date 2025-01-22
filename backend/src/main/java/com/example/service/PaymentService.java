package com.example.service;

import com.example.entity.PaymentEntity;
import com.example.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${toss.secretKey}")
    private String tossSecretKey;

    @Value("${toss.baseUrl}")
    private String tossBaseUrl;

    private final PaymentRepository paymentRepository;
    private final WebClient webClient;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.webClient = WebClient.builder()
                .baseUrl(tossBaseUrl)
                .defaultHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes()))
                .build();
    }

    /**
     * 결제 요청 처리
     */
    public Map<String, Object> requestPayment(PaymentEntity payment, String successUrl, String failUrl) {
        // PaymentEntity Cassandra에 저장
        paymentRepository.save(payment);

        // Toss Payments API 요청
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("orderId", payment.getOrderId());
        requestBody.put("orderName", payment.getOrderName());
        requestBody.put("amount", payment.getAmount());
        requestBody.put("successUrl", successUrl);
        requestBody.put("failUrl", failUrl);

        System.out.println("Toss API Request: " + requestBody);

        return webClient.post()
                .uri("/v1/payments")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * 결제 승인 처리
     */
    public Map<String, Object> confirmPayment(String paymentKey, String orderId, String orderName, Integer amount) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentKey", paymentKey);
        requestBody.put("orderId", orderId);
        requestBody.put("orderName", orderName);
        requestBody.put("amount", amount);

        return webClient.post()
                .uri("/v1/payments/confirm")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * 결제 정보 저장
     */
    public void savePaymentInfo(String paymentKey, String orderId, String orderName, Integer amount, String status, String userId) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrderId(orderId);
        payment.setPaymentKey(paymentKey);
        payment.setOrderName(orderName);
        payment.setAmount(amount);
        payment.setStatus(status);
        payment.setUserId(userId);
        payment.setCreatedAt(LocalDateTime.now());

        try {
            paymentRepository.save(payment);
            System.out.println("Payment saved successfully: " + payment);
        } catch (Exception e) {
            System.err.println("Error saving payment info to Cassandra: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Cassandra 저장 실패");
        }
    }
}
