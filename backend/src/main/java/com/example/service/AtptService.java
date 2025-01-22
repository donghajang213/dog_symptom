//package com.example.service;
//
//import com.example.entity.AtptEntity;
//import com.example.repository.AtptRepository;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Service
//public class AtptService {
//
//    private final AtptRepository atptRepository;
//
//    public AtptService(AtptRepository atptRepository) {
//        this.atptRepository = atptRepository;
//    }
//
//    public AtptEntity chatWithOllama(String userId, String atptInput) {
//        // Ollama 호출
//        String atptOutput = callOllamaAPI(atptInput);
//
//        // Cassandra에 저장
//        AtptEntity atptEntity = new AtptEntity();
//        atptEntity.setAtptId(UUID.randomUUID());
//        atptEntity.setUserId(userId);
//        atptEntity.setAtptInput(atptInput);
//        atptEntity.setAtptOutput(atptOutput);
//        atptEntity.setAtptTime(Instant.now());
//
//        return atptRepository.save(atptEntity);
//    }
//
//    private String callOllamaAPI(String prompt) {
//        String url = "http://localhost:11434/api/query";
//
//        // 요청 바디 생성
//        String requestBody = "{ \"model\": \"llama2\", \"prompt\": \"" + prompt + "\" }";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//
//        return response.getBody();
//    }
//}
