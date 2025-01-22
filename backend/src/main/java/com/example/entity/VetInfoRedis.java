package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@RedisHash("VetInfo")
public class VetInfoRedis implements Serializable {
    @Id
    private UUID vetId;
    private String name;
    private String address;
    private int consultationCount;


}
