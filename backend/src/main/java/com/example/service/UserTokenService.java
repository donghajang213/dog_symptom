package com.example.service;

import com.example.entity.UserTokenEntity;
import com.example.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserTokenService {

    private final UserTokenRepository tokenRepository;

    public UserTokenService(UserTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void updateTokens(String userId, int purchasedTokens) {
        UserTokenEntity tokenEntity = tokenRepository.findById(userId).orElse(new UserTokenEntity());

        System.out.println("Before Update - UserId: " + userId + ", TotalTokens: " + tokenEntity.getTotalTokens());

        // 기존 토큰 개수에 새로 구매한 토큰 추가
        tokenEntity.setUserId(userId);
        tokenEntity.setTotalTokens(tokenEntity.getTotalTokens() + purchasedTokens);
        tokenEntity.setUpdatedAt(LocalDateTime.now());

        // 저장 또는 업데이트
        try {
            tokenRepository.save(tokenEntity);
            System.out.println("After Update - TokenEntity: " + tokenEntity);
        } catch (Exception e) {
            System.err.println("Error saving tokens: " + e.getMessage());
        }
    }

    // 특정 사용자의 토큰 수 조회
    public int getTokensByUserId(String userId) {
        return tokenRepository.findById(userId)
                .map(UserTokenEntity::getTotalTokens)
                .orElse(0); // 데이터가 없으면 0 반환
    }

}
