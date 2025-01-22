package com.example.service.BannerService;

import com.example.entity.BannerEntity.BannerEntity;
import com.example.repository.BannerRepository.BannerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class BannerService {
    private final BannerRepository bannerRepository;

    // 파일 저장 디렉터리 경로를 나타내는 필드 추가
    private final Path bannerImageStorageLocation;

    // 생성자를 통해 저장 디렉터리 초기화
    public BannerService(BannerRepository bannerRepository, @Value("${file.banner-upload-dir}") String bannerUploadDir) {
        this.bannerRepository = bannerRepository;

        this.bannerImageStorageLocation = Paths.get(bannerUploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.bannerImageStorageLocation); // 디렉터리 생성
        } catch (IOException e) {
            throw new RuntimeException("배너 이미지 저장 디렉터리를 생성할 수 없습니다.", e);
        }
    }

    // 모든 배너를 가져오는 메서드
    public List<BannerEntity> getAllBanners() {
        return bannerRepository.findAll();
    }

    // 현재 활성화된 배너만 필터링
    public List<BannerEntity> getActiveBanners() {
        LocalDate currentDate = LocalDate.now();
        return bannerRepository.findAll().stream()
                .filter(banner -> banner.getStartDate().isBefore(currentDate)
                        && banner.getEndDate().isAfter(currentDate))
                .sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority())) // 우선순위 정렬
                .collect(Collectors.toList());
    }

    // 새 배너를 추가하는 메서드
    public BannerEntity addBanner(BannerEntity banner) {
        banner.setBannerId(UUID.randomUUID()); // 랜덤 UUID 생성
        return bannerRepository.save(banner);
    }

    // 배너를 삭제하는 메서드
    public void deleteBanner(UUID bannerId) {
        bannerRepository.deleteById(bannerId);
    }

    // 배너를 수정하는 메서드
    public BannerEntity updateBanner(UUID bannerId, BannerEntity updatedBanner) {
        BannerEntity existingBanner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다: " + bannerId));
        existingBanner.setImageUrl(updatedBanner.getImageUrl());
        existingBanner.setRedirectUrl(updatedBanner.getRedirectUrl());
        existingBanner.setPriority(updatedBanner.getPriority());
        existingBanner.setStartDate(updatedBanner.getStartDate());
        existingBanner.setEndDate(updatedBanner.getEndDate());
        return bannerRepository.save(existingBanner);
    }

    // 배너 이미지를 로드하는 메서드
    public Resource loadBannerImage(String fileName) {
        try {
            Path filePath = this.bannerImageStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("배너 이미지를 로드하는 중 오류가 발생했습니다: " + fileName, e);
        }
    }
}