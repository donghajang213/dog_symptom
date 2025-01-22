package com.example.service;

import com.example.entity.RedisUserEntity;
import com.example.repository.RedisUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisService {

    private final RedisUserRepository redisUserRepository;

    @Autowired
    public RedisService(RedisUserRepository redisUserRepository) {
        this.redisUserRepository = redisUserRepository;
    }

    // Redis에 사용자 세션 저장
    public void saveRedisUser(RedisUserEntity redisUserEntity) {
        redisUserRepository.save(redisUserEntity);
    }

    // Redis에서 사용자 세션 조회
    public Optional<RedisUserEntity> getRedisUserById(String userId) {
        return redisUserRepository.findById(userId);
    }

    // Redis에서 사용자 세션 삭제
    public void deleteRedisUserById(String userId) {
        redisUserRepository.deleteById(userId);
    }
}