package com.example.service;

import com.example.entity.VetEntity;
import com.example.entity.VetInfoRedis;
import com.example.repository.VetInfoRedisRepository;
import com.example.repository.VetRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class VetDataInitializer {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private VetInfoRedisRepository vetInfoRedisRepository;

    @PostConstruct
    public void init() {
        List<VetEntity> vets = vetRepository.findAll();
        for (VetEntity vet : vets) {
            VetInfoRedis vetInfoRedis = new VetInfoRedis();
            vetInfoRedis.setVetId(vet.getVetId());
            vetInfoRedis.setName(vet.getName());
            vetInfoRedis.setAddress(vet.getAddress());
            vetInfoRedis.setConsultationCount(vet.getConsultationCount());
            vetInfoRedisRepository.save(vetInfoRedis);
        }
    }
}
