package com.example.service.BannerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path bannerImageStorageLocation;
    private final Path eventImageStorageLocation;

    @Value("${server.base-url:http://localhost:8080}") // 서버의 기본 URL
    private String serverBaseUrl;

    // 생성자에서 배너와 이벤트 이미지 디렉터리를 각각 초기화
    public FileStorageService(
            @Value("${file.banner-upload-dir}") String bannerImageDir,
            @Value("${file.events-upload-dir}") String eventImageDir) {

        this.bannerImageStorageLocation = Paths.get(bannerImageDir).toAbsolutePath().normalize();
        this.eventImageStorageLocation = Paths.get(eventImageDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.bannerImageStorageLocation); // 배너 디렉터리 생성
            Files.createDirectories(this.eventImageStorageLocation); // 이벤트 디렉터리 생성
        } catch (Exception ex) {
            throw new RuntimeException("이미지 저장 디렉터리를 생성할 수 없습니다.", ex);
        }
    }

    // 배너 이미지 저장
    public String storeBannerImage(MultipartFile bannerImage) throws IOException {
        return storeImage(bannerImage, bannerImageStorageLocation, "/banners/");
    }

    // 이벤트 이미지 저장
    public String storeEventImage(MultipartFile eventImage) throws IOException {
        return storeImage(eventImage, eventImageStorageLocation, "/admin/events/");
    }

    // 공통 이미지 저장 로직
    private String storeImage(MultipartFile image, Path storageLocation, String urlPrefix) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있거나 null입니다.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_"); // 파일명 정리

        Path targetLocation = storageLocation.resolve(fileName);
        Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 파일 URL 생성
        String fileUrl = serverBaseUrl + urlPrefix + fileName;
        System.out.println("이미지 저장 경로 : " + targetLocation.toString());
        System.out.println("이미지 URL : " + fileUrl);

        return fileUrl; // 파일 URL 반환
    }

    // 배너 이미지 로드
    public Resource loadBannerImage(String fileName) {
        return loadImage(fileName, bannerImageStorageLocation);
    }

    // 이벤트 이미지 로드
    public Resource loadEventImage(String fileName) {
        return loadImage(fileName, eventImageStorageLocation); // 변경: eventImageStorageLocation 사용
    }

    // 공통 이미지 로드 로직
    private Resource loadImage(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지를 로드하는 중 오류가 발생했습니다: " + fileName, e);
        }
    }
}
