package com.example.service;

import com.example.entity.PetEntity;
import com.example.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;
    private final String uploadDir = "uploads/pet_photos";  //파일 업로드 기본 디렉토리

    public void savePet(PetEntity pet) {
        petRepository.save(pet);
    }

    public List<PetEntity> getPetsByUserId(String userId) {
        System.out.println("PetService: 조회할 사용자 ID: " + userId);

        List<PetEntity> pets = petRepository.findByUserId(userId);
        System.out.println("PetService: 조회된 반려동물 데이터: " + pets);
        return pets;
    }

    // 삭제
    public boolean deletePet(String userId, UUID petId) {
        Optional<PetEntity> petOptional = petRepository.findByUserIdAndPetId(userId, petId);
        if (petOptional.isPresent()) {
            petRepository.deleteByUserIdAndPetId(userId, petId);
            return true;
        } else {
            return false; // petId가 존재하지 않을 경우
        }
    }


    public String saveFile(MultipartFile file) {
        try {
            // 파일 이름 생성 (중복 방지를 위해 UUID 사용)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 업로드 디렉토리 경로 (Path 객체로 선언)
            Path uploadDir = Paths.get("uploads", "pet_photos");

            // 저장할 전체 경로
            Path targetPath = uploadDir.resolve(fileName).normalize();

            // 디렉토리 생성 (존재하지 않을 경우)
            Files.createDirectories(uploadDir);

            // 파일 저장
            Files.copy(file.getInputStream(), targetPath);

            // 저장된 경로를 문자열로 반환 (슬래시 표준화)
            return targetPath.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 수정
    public boolean editPet(String userId, PetEntity pet, MultipartFile photo) {
        Optional<PetEntity> existingPetOptional = petRepository.findByUserIdAndPetId(userId, pet.getPetId());
        if (existingPetOptional.isPresent()) {
            PetEntity existingPet = existingPetOptional.get();

            // 변경 가능한 필드 업데이트
            existingPet.setName(pet.getName());
            existingPet.setBreed(pet.getBreed());
            existingPet.setBirthDate(pet.getBirthDate());
            existingPet.setGender(pet.getGender());
            existingPet.setNeuteringStatus(pet.getNeuteringStatus());
            existingPet.setWeightKg(pet.getWeightKg());
            existingPet.setRegistrationNumber(pet.getRegistrationNumber());

            // 이미지 처리
            if (photo != null && !photo.isEmpty()) {
                try {
                    String filePath = saveFile(photo); // 파일 저장
                    existingPet.setPetImage(filePath); // 새 이미지 경로 설정
                } catch (Exception e) {
                    throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
                }
            }

            // 데이터 저장
            petRepository.save(existingPet);
            return true;
        } else {
            return false; // petId가 존재하지 않을 경우
        }
    }

    // ID로 반려동물 조회
    public PetEntity findById(UUID petId) {
        Optional<PetEntity> optionalPet = petRepository.findById(petId);
        return optionalPet.orElse(null); // 데이터가 없으면 null 반환
    }

}