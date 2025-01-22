package com.example.controller.BannerController;

import com.example.entity.BannerEntity.BannerEntity;
import com.example.service.BannerService.BannerService;
import com.example.service.BannerService.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/banners") // API 기본 경로
public class BannerController {

    private final BannerService bannerService;
    private final FileStorageService fileStorageService;

    public BannerController(BannerService bannerService, FileStorageService fileStorageService) {
        this.bannerService = bannerService;
        this.fileStorageService = fileStorageService;
    }

    // 모든 배너 가져오기
    @GetMapping
    public List<BannerEntity> getAllBanners() {
        return bannerService.getAllBanners();
    }

    // 현재 활성화된 배너 가져오기
    @GetMapping("/active")
    public List<BannerEntity> getActiveBanners() {
        return bannerService.getActiveBanners();
    }

    // 새 배너 추가 (관리자용)
    @PostMapping("/admin")
    public ResponseEntity<BannerEntity> addBanner(
            @RequestParam("imageFile") MultipartFile imageFile, // 이미지 파일 업로드
            @RequestParam("redirectUrl") String redirectUrl,
            @RequestParam("priority") int priority,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            // 이미지 저장 및 URL 생성
            String imageUrl = fileStorageService.storeBannerImage(imageFile);

            // 배너 엔티티 생성
            BannerEntity banner = new BannerEntity();
            banner.setBannerId(UUID.randomUUID()); // 고유 ID 생성
            banner.setImageUrl(imageUrl);
            banner.setRedirectUrl(redirectUrl);
            banner.setPriority(priority);
            banner.setStartDate(LocalDate.parse(startDate));
            banner.setEndDate(LocalDate.parse(endDate));

            // 배너 저장
            BannerEntity createdBanner = bannerService.addBanner(banner);
            return ResponseEntity.ok(createdBanner);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 배너 수정 (관리자용)
    @PutMapping("/admin/{bannerId}")
    public ResponseEntity<BannerEntity> updateBanner(
            @PathVariable UUID bannerId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, // 이미지 파일 (선택)
            @RequestParam("redirectUrl") String redirectUrl,
            @RequestParam("priority") int priority,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            // 기존 배너 가져오기
            BannerEntity existingBanner = bannerService.getAllBanners().stream()
                    .filter(b -> b.getBannerId().equals(bannerId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다: " + bannerId));

            // 새 이미지 업로드 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String newImageUrl = fileStorageService.storeBannerImage(imageFile);
                existingBanner.setImageUrl(newImageUrl);
            }

            // 다른 필드 업데이트
            existingBanner.setRedirectUrl(redirectUrl);
            existingBanner.setPriority(priority);
            existingBanner.setStartDate(LocalDate.parse(startDate));
            existingBanner.setEndDate(LocalDate.parse(endDate));

            // 배너 업데이트
            BannerEntity updatedBanner = bannerService.updateBanner(bannerId, existingBanner);
            return ResponseEntity.ok(updatedBanner);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 배너 삭제 (관리자용)
    @DeleteMapping("/admin/{bannerId}")
    public ResponseEntity<Void> deleteBanner(@PathVariable UUID bannerId) {
        bannerService.deleteBanner(bannerId);
        return ResponseEntity.noContent().build();
    }

    // **배너 이미지 제공 API**
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> serveBannerImage(@PathVariable String fileName) {
        try {
            // 파일 로드
            Resource resource = fileStorageService.loadBannerImage(fileName);

            // 파일이 존재하고 읽을 수 있을 때
            if (resource.exists() || resource.isReadable()) {
                // 파일 확장자 확인 (JPG, PNG 등)
                String contentType = Files.probeContentType(resource.getFile().toPath());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType)) // 파일 타입 자동 감지
                        .body(resource);
            } else {
                // 파일이 없을 경우
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // 서버 에러 응답
        }
    }
}