package com.example.service.UserInfoService;

import com.example.entity.UserInfoEntity.UserInfoEntity;
import com.example.repository.UserInfoRepository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    // 모든 유저 조회
    public List<UserInfoEntity> getAllUsers() {
        return userInfoRepository.findAll();
    }

    // 특정 유저 상세 조회
    public UserInfoEntity getUserById(String userId) {
        return userInfoRepository.findById(userId).orElse(null);
    }

    // 이름, 아이디, 역할을 한 번에 검색
    public List<UserInfoEntity> searchUsersByKeyword(String keyword) {
        return userInfoRepository.searchByKeyword(keyword);
    }
}
