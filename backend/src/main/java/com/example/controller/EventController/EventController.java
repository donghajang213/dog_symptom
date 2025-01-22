package com.example.controller.EventController;

import com.example.entity.EventEntity.EventEntity;
import com.example.service.EventService.EventService;
import com.example.service.BannerService.FileStorageService;
import org.springframework.core.io.Resource;
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
@RequestMapping("/admin/events")
public class EventController {

    private final EventService eventService;
    private final FileStorageService fileStorageService;

    public EventController(EventService eventService, FileStorageService fileStorageService) {
        this.eventService = eventService;
        this.fileStorageService = fileStorageService;
    }

    // 모든 이벤트 조회
    @GetMapping
    public List<EventEntity> getAllEvents() {
        return eventService.getAllEvents();
    }

    // 이벤트 추가
    @PostMapping
    public EventEntity addEvent(
            @RequestParam("eventTitle") String eventTitle,
            @RequestParam("eventDesc") String eventDesc,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "status", defaultValue = "진행중") String status) throws IOException {

        // 이미지 저장
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileStorageService.storeEventImage(imageFile);
        }

        // EventEntity 생성
        EventEntity event = new EventEntity();
        event.setEventId(UUID.randomUUID()); // 고유 ID 생성
        event.setEventTitle(eventTitle);
        event.setEventDesc(eventDesc);
        event.setStartDate(LocalDate.parse(startDate));
        event.setEndDate(LocalDate.parse(endDate));
        event.setImageUrl(imageUrl);
        event.setStatus(status);

        // 저장
        return eventService.addEvent(event);
    }

    // 이벤트 수정
    @PutMapping("/{eventId}")
    public EventEntity updateEvent(
            @PathVariable UUID eventId,
            @RequestParam("eventTitle") String eventTitle,
            @RequestParam("eventDesc") String eventDesc,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "status", defaultValue = "진행중") String status) throws IOException {

        // 기존 이벤트 가져오기
        EventEntity existingEvent = eventService.getEventById(eventId);

        // 이미지 저장 (새로운 이미지가 있을 경우)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeEventImage(imageFile);
            existingEvent.setImageUrl(imageUrl);
        }

        // 다른 필드 업데이트
        existingEvent.setEventTitle(eventTitle);
        existingEvent.setEventDesc(eventDesc);
        existingEvent.setStartDate(LocalDate.parse(startDate));
        existingEvent.setEndDate(LocalDate.parse(endDate));
        existingEvent.setStatus(status);

        // 업데이트
        return eventService.updateEvent(existingEvent, imageFile);
    }

    // 이벤트 삭제
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable UUID eventId) {
        eventService.deleteEvent(eventId);
    }

    // 이미지 제공
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> serveEventImage(@PathVariable String fileName) {
        try {
            // 1. Load event image file
            Resource resource = fileStorageService.loadEventImage(fileName);

            // 2. Check if the file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                // 3. Determine the content type of the file (e.g., JPG, PNG, etc.)
                String contentType = Files.probeContentType(resource.getFile().toPath());

                // 4. Return response with proper content type and resource
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                // If the file does not exist, return 404
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            // Return 500 in case of server error
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping("/ongoing")
    public List<EventEntity> getOngoingEvents() {
        return eventService.getOngoingEvents(); // 진행 중인 이벤트 가져오기
    }

    @GetMapping("/ended")
    public List<EventEntity> getEndedEvents() {
        return eventService.getEndedEvents(); // 종료된 이벤트 가져오기
    }

}