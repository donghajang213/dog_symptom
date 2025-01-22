//package com.example.service;
//
//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.client5.http.config.RequestConfig;
//import org.apache.hc.core5.util.Timeout;
//import org.apache.hc.core5.http.io.entity.StringEntity;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Service
//public class TossApiService {
//
//    private String secretKey = "test_sk_kYG57Eba3G2qLqpW5Oxw8pWDOxmA";
//
//    public void approvePayment(String paymentKey, String orderId, Integer amount) throws Exception {
//        String url = "https://api.tosspayments.com/v1/payments/confirm";
//
//        // RequestConfig로 타임아웃 설정
//        RequestConfig config = RequestConfig.custom()
//                .setConnectTimeout(Timeout.ofSeconds(30))  // 연결 타임아웃
//                .setResponseTimeout(Timeout.ofSeconds(30)) // 응답 타임아웃
//                .build();
//
//        try (CloseableHttpClient client = HttpClients.custom()
//                .setDefaultRequestConfig(config) // 설정 적용
//                .build()) {
//
//            HttpPost post = new HttpPost(url);
//            // 인증 헤더 설정
//            post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
//            post.setHeader("Content-Type", "application/json");
//
//            // 요청 바디 설정
//            String body = String.format("{\"paymentKey\":\"%s\", \"orderId\":\"%s\", \"amount\":%d}", paymentKey, orderId, amount);
//            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
//
//            try (CloseableHttpResponse response = client.execute(post)) {
//                int statusCode = response.getCode(); // 상태 코드 확인
//                if (statusCode != 200) {
//                    throw new Exception("결제 승인 실패: HTTP 상태 코드 " + statusCode);
//                }
//                // 응답 처리 (필요 시 추가)
//            }
//        }
//    }
//}
