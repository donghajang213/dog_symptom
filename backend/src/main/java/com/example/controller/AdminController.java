package com.example.controller;

import com.example.entity.UserEntity;
import com.example.entity.VetEntity;
import com.example.service.UserService;
import com.example.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private VetService vetService;

    @GetMapping("/permissions")
    public List<UserEntity> getPermissionRequests() {
        // 권한 요청 리스트 가져오기 (PENDING 상태의 요청)
        return userService.getPendingUsers();
    }

    @Transactional
    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveUserRole(@PathVariable String userId, @RequestBody Map<String, String> roleMap) {
        String role = roleMap.get("role");
        System.out.println("역할 승인 요청: userId=" + userId + ", role=" + role); // 로그 추가

        if (!"VET".equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 역할입니다!");
        }

        try {
            UserEntity user = userService.getUserByIdOrThrow(userId);
            if (!"PENDING".equals(user.getUserRole())) {
                return ResponseEntity.badRequest().body("유효하지 않은 상태입니다.");
            }
            // UUID를 생성하여 vet_id로 사용
            UUID vetId = UUID.randomUUID();

            VetEntity vet = new VetEntity();
            vet.setVetId(vetId); // VetEntity의 vetId 설정
            vet.setName(user.getName());
            vet.setPhoneNumber(user.getPhoneNumber());
            vet.setEmail(user.getEmail());
            vet.setAddress(user.getAddress());
            vet.setVetImage(user.getVetImage());
            vet.setConsultationCount(0); // 초기 상담 건수
            vet.setVetRating(0); // 초기 평점
            vet.setReviewCount(0); // 초기 리뷰

            vetService.saveVet(vet);
            System.out.println("VetEntity 저장 완료: " + vet); // 로그 추가

            // 역할 업데이트와 저장은 UserService에서 처리됨(덮어쓰지 않음)
            return userService.approveUserRole(userId, role);

        } catch (Exception e) {
            // 예외 처리 및 로그 추가
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }


    @PutMapping("/rejectRole/{userId}")
    public ResponseEntity<?> rejectUserRole(@PathVariable String userId){
        return userService.rejectUserRole(userId);
    }


}
