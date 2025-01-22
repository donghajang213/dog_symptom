//package com.example.controller;
//
//import com.example.entity.AtptEntity;
//import com.example.service.AtptService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/atpt")
//public class AtptController {
//
//    private final AtptService atptService;
//
//    public AtptController(AtptService atptService) {
//        this.atptService = atptService;
//    }
//
//    @PostMapping("/chat")
//    public AtptEntity chatWithOllama(@RequestParam String userId, @RequestParam String atptInput) {
//        System.out.println("userId: " + userId);
//        System.out.println("atptInput: " + atptInput);
//
//        return atptService.chatWithOllama(userId, atptInput);
//    }
//}
