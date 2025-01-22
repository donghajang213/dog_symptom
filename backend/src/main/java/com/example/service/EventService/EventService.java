package com.example.service.EventService;

import com.example.entity.EventEntity.EventEntity;
import com.example.repository.EventRepository.EventRepository;
import com.example.service.BannerService.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;

    public EventService(EventRepository eventRepository, FileStorageService fileStorageService) {
        this.eventRepository = eventRepository;
        this.fileStorageService = fileStorageService;
    }

    // 모든 이벤트 조회
    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    // 특정 이벤트 조회 (ID로 조회)
    public EventEntity getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));
    }

    // 이벤트 추가
    public EventEntity addEvent(EventEntity event) {
        event.setEventId(UUID.randomUUID()); // UUID 생성
        if (event.getStatus() == null) {
            event.setStatus("진행중"); // 기본 상태 설정
        }
        if (event.getStartDate() == null) {
            event.setStartDate(LocalDate.now()); // 기본 시작 날짜 설정
        }
        if (event.getEndDate() == null) {
            event.setEndDate(LocalDate.now().plusDays(7)); // 기본 종료 날짜 설정 (7일 후)
        }
        return eventRepository.save(event);
    }

    // 이벤트 수정
    public EventEntity updateEvent(EventEntity updatedEvent, MultipartFile imageFile) throws IOException {
        // 기존 이벤트 조회
        EventEntity existingEvent = eventRepository.findById(updatedEvent.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + updatedEvent.getEventId()));

        // 이미지 파일 처리 (새로운 이미지가 있으면 저장)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeEventImage(imageFile);
            existingEvent.setImageUrl(imageUrl);
        }

        // 필드 업데이트
        existingEvent.setEventTitle(updatedEvent.getEventTitle());
        existingEvent.setEventDesc(updatedEvent.getEventDesc());
        existingEvent.setStartDate(updatedEvent.getStartDate());
        existingEvent.setEndDate(updatedEvent.getEndDate());
        existingEvent.setStatus(updatedEvent.getStatus());

        // 저장 및 반환
        return eventRepository.save(existingEvent);
    }

    // 이벤트 삭제
    public void deleteEvent(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    public List<EventEntity> getOngoingEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.getStatus().equals("진행중")
                        && LocalDate.now().isAfter(event.getStartDate())
                        && LocalDate.now().isBefore(event.getEndDate()))
                .collect(Collectors.toList());
    }
    public List<EventEntity> getEndedEvents() {
        return eventRepository.findAll().stream()
                .filter(event -> event.getStatus().equals("종료")
                        || LocalDate.now().isAfter(event.getEndDate()))
                .collect(Collectors.toList());
    }

}