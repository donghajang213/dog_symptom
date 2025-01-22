package com.example.controller.UserInfoController;

import com.example.entity.UserInfoEntity.UserInfoEntity;
import com.example.service.UserInfoService.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserInfoController
 *
 * 유저 정보를 관리하는 컨트롤러로, 모든 유저와 특정 유저의 상세 정보를 반환합니다.
 */
@RestController
@RequestMapping("/admin/userinfo")
public class UserInfoController {

    // UserInfoService를 통해 비즈니스 로직 처리
    private final UserInfoService userInfoService;

    /**
     * 생성자 주입 방식으로 UserInfoService를 주입받음
     *
     * @param userInfoService 유저 정보 관련 서비스 클래스
     */
    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }


    @GetMapping
    public List<UserInfoEntity> getAllUsers() {
        return userInfoService.getAllUsers();
    }

    @GetMapping("/admin/userinfo/{userId}") // "{userId}"
    public UserInfoEntity getUserById(@PathVariable String userId) {
        return userInfoService.getUserById(userId);
    }

    // 이름, 아이디, 역할을 포함한 검색
    @GetMapping("/search")
    public List<UserInfoEntity> searchByKeyword(@RequestParam String keyword) {
        return userInfoService.searchUsersByKeyword(keyword);
    }

}