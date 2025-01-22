package com.example.service;

import com.example.entity.LoginResult;
import com.example.entity.PetEntity;
import com.example.entity.RedisUserEntity;
import com.example.entity.UserEntity;
import com.example.repository.PetRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final RedisService redisService; // RedisService 추가
    private final BCryptPasswordEncoder passwordEncoder;
    private final Path fileStorageLocation;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가


    @Autowired
    public UserService(
            UserRepository userRepository,

            PetRepository petRepository ,
            RedisService redisService, // RedisService DI
            BCryptPasswordEncoder passwordEncoder,
            RedisTemplate<String, Object> redisTemplate,
            @Value("${file.upload-dir}") String uploadDir) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.redisService = redisService; // RedisService 주입
        this.passwordEncoder = passwordEncoder; // DI를 통해 Bean 주입
        this.redisTemplate = redisTemplate;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        // 경로 생성
        try {
            Files.createDirectories(fileStorageLocation);
            if (!Files.isWritable(fileStorageLocation)) {
                throw new IOException("지정된 디렉터리에 쓸 수 없습니다: " + fileStorageLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("디렉터리 생성 또는 접근 실패", e);
        }
    }


    // 수의사 이미지 저장 로직
    public String storeVetImage(MultipartFile vetImage) throws IOException {
        if (vetImage == null || vetImage.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있거나 null입니다.");
        }
        String fileName = UUID.randomUUID().toString() + "_" + vetImage.getOriginalFilename();
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_"); // 파일명 정리

        Path targetLocation = fileStorageLocation.resolve(fileName);

        Files.copy(vetImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("이미지 저장 경로 : " + targetLocation.toString());
        return fileName; // 저장된 파일 경로 반환
    }

    // 사용자 저장 (회원가입)
    public UserEntity saveUser(UserEntity user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("패스워드가 널값입니다");
        }
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 가입일 설정 (현재 시간으로 설정)
        user.setUserRegdate(LocalDateTime.now());  // 가입 시 현재 날짜와 시간을 설정

        System.out.println("저장 전 역할: " + user.getUserRole()); // 역할 로그 추가

        // 사용자 역할에 따라 필드 설정
        if ("PENDING".equals(user.getUserRole()) && (user.getVetImage() == null || user.getVetImage().isEmpty())) {
            user.setVetImage("default_image.jpg");
        }

        UserEntity savedUser = userRepository.save(user); // Cassandra에 데이터 저장
        System.out.println("저장 후 역할: " + savedUser.getUserRole()); // 역할 저장 후 로그

        return savedUser;
    }


    // PENDING 상태의 사용자 목록 가져오기
    public List<UserEntity> getPendingUsers() {
        return userRepository.findByUserRole("PENDING");
    }

    // 사용자 역할 승인
    public ResponseEntity<?> approveUserRole(String userId, String role) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if ("PENDING".equals(user.getUserRole())) {
                user.setUserRole(role);
                userRepository.updateUserRole(user.getUserId(), role);
                System.out.println("역할 변경 완료: " + user.getUserRole());
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.badRequest().body("승인되지 않은 역할 변경 요청입니다.");
            }
        } else {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }

    // 사용자 역할 거부
    public ResponseEntity<?> rejectUserRole(String userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if ("PENDING".equals(user.getUserRole())) {
                user.setUserRole("REJECTED");
                userRepository.save(user);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.badRequest().body("거부되지 않은 역할 변경 요청입니다.");
            }
        } else {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }

//    // 로그인 성공 시 Redis에 사용자 세션 저장
//    public void saveSessionToRedis(UserEntity user) {
//        RedisUserEntity redisUserEntity = new RedisUserEntity();
//        redisUserEntity.setUserId(user.getUserId());
//        redisUserEntity.setAddress(user.getAddress());
//        redisUserEntity.setEmail(user.getEmail());
//        redisUserEntity.setName(user.getName());
//        redisUserEntity.setUserRole(user.getUserRole());
//        redisUserEntity.setBirthDate(user.getBirthDate());
//        redisUserEntity.setPhoneNumber(user.getPhoneNumber());
//        redisUserEntity.setDetailedAddress(user.getDetailedAddress());
//        redisService.saveRedisUser(redisUserEntity); // Redis 저장
//    }

    // Redis에서 세션 가져오기
    public Optional<RedisUserEntity> getSessionFromRedis(String userId) {
        return redisService.getRedisUserById(userId);
    }

    // Redis에서 세션 삭제
    public void deleteSessionFromRedis(String userId) {
        redisService.deleteRedisUserById(userId);
    }

    // 로그인 검증
    public LoginResult validateLogin(String userId, String rawPassword) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if ("REJECTED".equals(user.getUserRole())) {
                return new LoginResult(false, "계정이 거절되었습니다.", null);
            } else if ("PENDING".equals(user.getUserRole())) {
                return new LoginResult(false, "계정 승인이 완료되지 않았습니다.", null);
            } else if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return new LoginResult(true, "로그인 성공", user);
            } else {
                return new LoginResult(false, "아이디 또는 비밀번호가 올바르지 않습니다.", null);
            }
        }
        return new LoginResult(false, "아이디 또는 비밀번호가 올바르지 않습니다.", null);

    }

    // 사용자 ID로 사용자 정보 조회
    public Optional<UserEntity> getUserById(String userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return Optional.of(user.get());
        } else {
            throw new RuntimeException("해당 ID로 등록된 사용자가 없습니다.");
        }
    }

    // 사용자 ID로 사용자 정보 조회하고, 사용자가 없으면 예외를 던짐
    public UserEntity getUserByIdOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID로 등록된 사용자가 없습니다."));
    }



//     파일 저장 로직을 서비스 내부로 이동
    public String saveImage(MultipartFile vetimage) throws IOException {
        if (vetimage == null || vetimage.isEmpty()) {
            System.out.println("이미지가 비어 있습니다.");
            return null;
        }
        String fileName = UUID.randomUUID() + "_" + vetimage.getOriginalFilename();
        Path path = Paths.get(fileStorageLocation + "/" + fileName);
        Files.createDirectories(path.getParent());
        Files.copy(vetimage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName; // 상대 경로 반환
    }

    public UserEntity updateUser(String userId, String name, LocalDate birthDate, String email,
                                 String phoneNumber, String address, String detailedAddress, String vetImage) {
        // 사용자 조회
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        UserEntity user = userOptional.get();

        // 사용자 정보 업데이트
        if (name != null) user.setName(name);
        if (birthDate != null) user.setBirthDate(birthDate);
        if (email != null) user.setEmail(email);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if (address != null) user.setAddress(address);
        if (detailedAddress != null) user.setDetailedAddress(detailedAddress);

        if (vetImage != null && !vetImage.isEmpty()) {
            user.setVetImage(vetImage);
        }

        // DB에 저장
        System.out.println("Updated vetImage: " + user.getVetImage());
        return userRepository.save(user);
    }


    // 사용자 삭제
    public boolean deleteUser(String userId) {
        try {
            // 사용자 조회
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return false; // 사용자 존재하지 않음
            }

            // Step 1: 사용자와 연관된 펫 데이터 삭제
            petRepository.deleteAllByUserId(userId); // user_id로 연관된 모든 펫 삭제


            // Step 2: 사용자 데이터 삭제
            userRepository.deleteById(userId);


            // Step 3: Redis 캐시 삭제
            redisTemplate.delete("user:" + userId);

            return true;

        } catch (Exception e) {
            // 예외 처리 및 로그 출력
            System.err.println("회원 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
    public boolean isUserIdAvailable(String userId) {
        return userRepository.findUserId(userId).isEmpty(); // Optional 사용
    }
}