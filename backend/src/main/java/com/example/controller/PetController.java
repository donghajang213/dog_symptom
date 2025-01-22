package com.example.controller;

import com.example.entity.PetEntity;
import com.example.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class); // 로그 생성

    @Autowired
    private final PetService petService;
    @Autowired
    private HttpSession session;  // HttpSession을 통해 세션에 접근
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPet(
            @RequestParam("name") String name,
            @RequestParam("breed") String breed,
            @RequestParam("birthDate") String birthDateString,
            @RequestParam("gender") String gender,
            @RequestParam("neuteringStatus") String neuteringStatus,
            @RequestParam("weightKg") BigDecimal weightKg,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            // String을 LocalDate로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthDate = LocalDate.parse(birthDateString, formatter);

            // 세션에서 user_id 가져오기
            String userId = (String) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User ID is missing in the session. Please log in first.");
            }

            UUID petId = UUID.randomUUID();

            // 데이터 처리 로직
            PetEntity pet = new PetEntity();
            pet.setPetId(petId);
            pet.setUserId(userId);
            pet.setName(name);
            pet.setBreed(breed);
            pet.setBirthDate(birthDate);
            pet.setGender(gender);
            pet.setNeuteringStatus(neuteringStatus);
            pet.setWeightKg(weightKg);
            pet.setRegistrationNumber(registrationNumber);

            // 이미지 파일 처리
            if (photo != null && !photo.isEmpty()) {
                String filePath = saveFile(photo);
                pet.setPetImage(filePath);
            }

            // 데이터 저장
            petService.savePet(pet);

            // 성공 응답 반환
            return ResponseEntity.ok("반려동물 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("반려동물 등록 중 오류가 발생했습니다.");
        }
    }

    // 파일을 로컬 디렉토리에 저장하고 파일 경로를 반환
    private String saveFile(MultipartFile file) {
        try {
            // 파일 이름에 UUID를 추가하여 중복되지 않게 저장
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/pet_photos", fileName); // 파일을 'uploads/pet_photos' 폴더에 저장
            Files.createDirectories(path.getParent()); // 디렉토리가 없으면 생성
            Files.copy(file.getInputStream(), path); // 파일 저장

            logger.info("파일 저장 성공: {}", path.toString());  // 파일 저장 경로 로그 출력
            return path.toString(); // 파일 경로 반환
        } catch (IOException e) {
            logger.error("파일 저장 중 오류 발생: ", e);  // 파일 저장 오류 로그 출력
            return null;
        }
    }

    // 삭제 요청
    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<String> deletePet(@PathVariable UUID petId) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        boolean isDeleted = petService.deletePet(userId, petId);
        if (isDeleted) {
            return ResponseEntity.ok("반려동물이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("반려동물을 찾을 수 없습니다.");
        }
    }

    // 수정 요청
    @PutMapping("/edit")
    public ResponseEntity<Map<String, String>> editPet(
            @RequestParam("petId") UUID petId,
            @RequestParam("name") String name,
            @RequestParam("breed") String breed,
            @RequestParam("birthDate") String birthDateString,
            @RequestParam("gender") String gender,
            @RequestParam("neuteringStatus") String neuteringStatus,
            @RequestParam("weightKg") BigDecimal weightKg,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            PetEntity pet = new PetEntity();
            pet.setPetId(petId);
            pet.setName(name);
            pet.setBreed(breed);
            pet.setBirthDate(LocalDate.parse(birthDateString));
            pet.setGender(gender);
            pet.setNeuteringStatus(neuteringStatus);
            pet.setWeightKg(weightKg);
            pet.setRegistrationNumber(registrationNumber);

            boolean isEdited = petService.editPet(userId, pet, photo);
            if (isEdited) {
                return ResponseEntity.ok(Map.of("message", "반려동물 정보가 수정되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "반려동물을 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "반려동물 수정 중 오류가 발생했습니다."));
        }
    }

}