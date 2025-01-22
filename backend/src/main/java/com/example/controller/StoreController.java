package com.example.controller;

import com.example.entity.StoreEntity.StoreEntity;
import com.example.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final String fileStorageLocation = "uploads/store"; // 이미지 경로 설정
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<StoreEntity> saveStore(
            @RequestPart("store") StoreEntity storeEntity,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            StoreEntity savedStore = storeService.saveStore(storeEntity, imageFile);
            return ResponseEntity.ok(savedStore);
        } catch (Exception e) {
            logger.error("상품 저장 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadStoreImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장
            String filePath = storeService.saveImage(file);
            return ResponseEntity.ok(filePath);
        } catch (Exception e) {
            logger.error("이미지 업로드 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    // 이미지 반환
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getStoreImage(@PathVariable String fileName) {
        try {
            // 이미지 경로 설정
            Path filePath = Paths.get("uploads/store").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                logger.error("이미지를 찾을 수 없습니다: " + fileName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("이미지 반환 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 모든 상품 조회
    @GetMapping
    public ResponseEntity<List<StoreEntity>> getAllStores() {
        try {
            List<StoreEntity> stores = storeService.getAllStores();
            return ResponseEntity.ok(stores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // 상품 삭제
    @DeleteMapping("/{productCode}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productCode) {
        try {
            storeService.deleteProduct(productCode);
            return ResponseEntity.ok("상품이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 상품 랭킹 조회
    @GetMapping("/ranking/product")
    public ResponseEntity<List<Map<String, Object>>> getProductRanking() {
        try {
            List<Map<String, Object>> productRanking = storeService.getProductRanking();
            return ResponseEntity.ok(productRanking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }

    // 제조사 랭킹 조회
    @GetMapping("/ranking/manufacturer")
    public ResponseEntity<List<Map<String, Object>>> getManufacturerRanking() {
        try {
            List<Map<String, Object>> manufacturerRanking = storeService.getManufacturerRanking();
            return ResponseEntity.ok(manufacturerRanking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }
}
