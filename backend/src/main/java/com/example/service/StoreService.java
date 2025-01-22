package com.example.service;

import com.example.entity.StoreEntity.StoreEntity;
import com.example.repository.StoreRepository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);
    private final StoreRepository storeRepository;
    private final String fileStorageLocation = "uploads/store"; // 상품 이미지를 저장할 경로


    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;

        // 파일 저장 경로가 존재하지 않으면 생성
        try {
            Path storagePath = Paths.get(fileStorageLocation);
            Files.createDirectories(storagePath);
            logger.info("파일 저장 디렉토리 생성됨: {}", fileStorageLocation);
        } catch (IOException e) {
            logger.error("파일 저장 디렉토리 생성 실패: ", e);
            throw new RuntimeException("파일 저장 디렉토리 생성 실패", e);
        }
    }

    // 이미지 저장 로직
    public String saveImage(MultipartFile file) { // 메서드 이름을 saveImage로 변경
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads", "store");
            Path targetPath = uploadDir.resolve(fileName).normalize();
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), targetPath);
            return targetPath.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // StoreEntity 저장 로직
    public StoreEntity saveStore(StoreEntity storeEntity, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // 이미지 파일 저장 후 URL 설정
                String imageUrl = saveImage(imageFile);
                storeEntity.setImageUrl(imageUrl);
            }
            return storeRepository.save(storeEntity); // Cassandra 테이블에 저장
        } catch (Exception e) {
            logger.error("Store 저장 중 오류 발생: ", e);
            throw new RuntimeException("Store 저장 중 오류 발생", e);
        }
    }

    // 모든 상품 조회
    public List<StoreEntity> getAllStores() {
        return storeRepository.findAll(); // Cassandra 테이블에서 모든 데이터 조회
    }

    // 상품 삭제
    public void deleteProduct(String productCode) {
        if (storeRepository.existsById(productCode)) {
            storeRepository.deleteById(productCode);
        } else {
            throw new RuntimeException("해당 상품을 찾을 수 없습니다.");
        }
    }

    // 상품별 랭킹 로직
    public List<Map<String, Object>> getProductRanking() {
        return storeRepository.findAll().stream()
                .collect(Collectors.groupingBy(StoreEntity::getProductName, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productName", entry.getKey());
                    map.put("productCount", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("productCount"), (Long) a.get("productCount")))
                .collect(Collectors.toList());
    }

    // 제조사별 랭킹 로직
    public List<Map<String, Object>> getManufacturerRanking() {
        return storeRepository.findAll().stream()
                .collect(Collectors.groupingBy(StoreEntity::getManufacturerId, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("manufacturerName", entry.getKey());
                    map.put("productCount", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("productCount"), (Long) a.get("productCount")))
                .collect(Collectors.toList());
    }
}
