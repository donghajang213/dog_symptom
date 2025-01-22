package com.example.controller;

import com.example.entity.ConsultationRequest;
import com.example.entity.UserEntity;
import com.example.entity.VetEntity;
import com.example.repository.ConsultationRequestRepository;
import com.example.repository.VetRepository;
import com.example.service.UserService;
import com.example.service.VetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/vets")
public class VetController {

    @Autowired
    private VetService vetService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;

    @Autowired
    private VetRepository vetRepository;

    // 특정 Vet 정보 조회
    @GetMapping("/{vetId}")
    public VetEntity getVet(@PathVariable UUID vetId) {
        Optional<VetEntity> vetOptional = vetService.getVetById(vetId);

        if (vetOptional.isPresent()) {
            VetEntity vet = vetOptional.get();
            return vet;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VET을 찾을 수 없습니다!");
        }
    }

    @GetMapping
    public ResponseEntity<List<VetEntity>> getVets(@RequestParam double userLat,
                                                   @RequestParam double userLon,
                                                   @RequestParam String sortCriteria) {
        List<VetEntity> sortedVets;
        if ("distance".equals(sortCriteria)) {
            sortedVets = vetService.getVetsSortedByDistance(userLat, userLon);
        } else {
            sortedVets = vetService.getVetsSortedByCriteria(sortCriteria);
        }
        return ResponseEntity.ok(sortedVets);
    }
}