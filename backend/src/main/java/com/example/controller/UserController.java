package com.example.controller;

import com.example.entity.LoginResult;
import com.example.entity.PetEntity;
import com.example.entity.UserEntity;
import com.example.entity.RedisUserEntity;
import com.example.repository.UserRepository;
import com.example.service.PetService;
import com.example.service.RedisService;
import com.example.service.UserService;

import com.example.service.UserTokenService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PetService petService;
    private final UserService userService;
    private final RedisService redisService;
    private final UserTokenService userTokenService;

    @Autowired
    public UserController(UserRepository userRepository, PetService petService, UserService userService, RedisService redisService, UserTokenService userTokenService) {
        this.userRepository = userRepository;
        this.petService = petService;
        this.userService = userService;
        this.redisService = redisService;
        this.userTokenService = userTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestPart("user") UserEntity user,
                                        @RequestPart(value = "vetImage", required = false) MultipartFile vetImage) {

        System.out.println("user 데이터: " + user.toString());
        System.out.println("받은 역할: " + user.getUserRole());

        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            System.out.println("유효성 검사 실패 : 아이디");
            return ResponseEntity.badRequest().body("유효성 검사 실패: 아이디는 필수 항목입니다.");
        }

        if (user.getUserRole() == null || user.getUserRole().isEmpty()) {
            System.out.println("유효성 검사 실패 : 역할");
            return ResponseEntity.badRequest().body("유효성 검사 실패: 역할은 필수 항목입니다.");
        }

        switch (user.getUserRole().toUpperCase()) {
            case "VET":
                if (user.getVetLicense() == null || user.getVetLicense().isEmpty()) {
                    System.out.println("유효성 검사 실패: 수의사 면허번호");
                    return ResponseEntity.badRequest().body("유효성 검사 실패: 수의사 면허번호는 필수 항목입니다.");
                }
                if (vetImage == null || vetImage.isEmpty()) {
                    System.out.println("유효성 검사 실패 : 수의사 이미지");
                    return ResponseEntity.badRequest().body("유효성 검사 실패: 수의사 이미지는 필수 항목입니다.");
                }
                user.setUserRole("PENDING");
                break;
            case "SELLER":
                user.setUserRole("PENDING");
                break;
            case "GENERAL":
            case "CUSTOMER":
                user.setUserRole("CUSTOMER");
                break;
            default:
                System.out.println("잘못된 역할");
                return ResponseEntity.badRequest().body("유효성 검사 실패: 잘못된 역할입니다.");
        }

        try {
            if ("PENDING".equals(user.getUserRole()) && vetImage != null) {
                String imagePath = userService.storeVetImage(vetImage);
                user.setVetImage(imagePath);
            }

            UserEntity savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 파일 업로드 : " + e.getMessage());
            return ResponseEntity.badRequest().body("유효하지 않은 파일 업로드: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("서버 내부 오류 : " + e.getMessage());
            return ResponseEntity.status(500).body("서버 내부 오류: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserEntity loginRequest, HttpSession session) {
        String userId = loginRequest.getUserId();
        String rawPassword = loginRequest.getPassword();

        LoginResult loginResult = userService.validateLogin(userId, rawPassword);
        if (loginResult.isSuccess()) {
            UserEntity user = loginResult.getUser();
            session.setAttribute("userId", userId);
            return ResponseEntity.ok(loginResult.getUser());
        } else {
            return ResponseEntity.status(401).body(Map.of("message", loginResult.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // Cassandra에서 사용자 정보 조회
            Optional<UserEntity> userOptional = userService.getUserById(userId);
            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                List<PetEntity> pets = petService.getPetsByUserId(userId);
                int totalTokens = userTokenService.getTokensByUserId(userId); // 토큰 갯수 조회
                return ResponseEntity.ok(Map.of("user", user, "pets", pets, "totalTokens", totalTokens));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "사용자 정보 조회 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(400).body("세션이 없습니다.");
        }

        redisService.deleteRedisUserById(userId);
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam("userInfo") String userInfoJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // JSON 문자열을 Map으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> userInfo = objectMapper.readValue(userInfoJson, new TypeReference<>() {});

            String name = userInfo.get("name");
            String email = userInfo.get("email");
            String phoneNumber = userInfo.get("phoneNumber");
            String address = userInfo.get("address");
            String detailedAddress = userInfo.get("detailedAddress");
            String birthDateString = userInfo.get("birthDate");
            LocalDate birthDate = birthDateString != null && !birthDateString.isEmpty()
                    ? LocalDate.parse(birthDateString)
                    : null;

            String vetImage = null;
            if (image != null && !image.isEmpty()) {
                vetImage = userService.saveImage(image);
                System.out.println("Saved vetImage path: " + vetImage);
            }

            UserEntity updatedUser = userService.updateUser(
                    userId, name, birthDate, email, phoneNumber, address, detailedAddress, vetImage);

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "회원 정보 수정 중 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            boolean isDeleted = userService.deleteUser(userId);
            if (isDeleted) {
                session.invalidate();
                return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "사용자를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 탈퇴 중 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<?> deleteMultipleUsers(@RequestBody Map<String, List<String>> request) {
        List<String> userIds = request.get("ids");

        if (userIds == null || userIds.isEmpty()) {
            return ResponseEntity.badRequest().body("삭제할 사용자 ID 목록이 비어 있습니다.");
        }

        try {
            for (String userId : userIds) {
                userService.deleteUser(userId); // 개별 사용자 삭제
            }
            return ResponseEntity.ok(Map.of("message", "선택된 사용자들이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "사용자 삭제 중 오류가 발생했습니다.", "error", e.getMessage()));
        }
    }


    @GetMapping("/checkUserId")
    public ResponseEntity<Map<String, Object>> checkUserId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();

        if (userId == null || userId.isEmpty()) {
            response.put("available", false);
            response.put("message", "아이디를 입력하세요.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isAvailable = userService.isUserIdAvailable(userId);
        if (isAvailable) {
            response.put("available", true);
            response.put("message", "사용 가능한 아이디입니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("available", false);
            response.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.ok(response);
        }
    }

}