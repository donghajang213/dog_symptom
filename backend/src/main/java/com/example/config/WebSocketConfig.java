package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker // 이 어노테이션이 중요
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 메시지 브로커 경로를 설정
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");  // 클라이언트가 보내는 메시지 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 수 있는 WebSocket 엔드포인트 설정
        registry.addEndpoint("/socket.io") // 첫 번째 엔드포인트
                .setAllowedOrigins("*") // 모든 도메인 허용
                .withSockJS(); // SockJS 지원

        registry.addEndpoint("/websocket") // 두 번째 엔드포인트
                .setAllowedOriginPatterns("*") // 모든 도메인 패턴 허용
                .withSockJS(); // SockJS 지원
    }
}
