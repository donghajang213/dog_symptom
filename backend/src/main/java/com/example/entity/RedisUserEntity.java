package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@RedisHash("RedisUserEntity") // Redis에 저장될 키를 정의
public class RedisUserEntity {

    @Id
    private String userId; // 사용자 ID (Redis의 키 역할)

    private String address;
    private String detailedAddress;
    private LocalDate birthDate;
    private String email;
    private String gender;
    private String name;
    private String phoneNumber;
    private String bankAccount; // 계좌번호
    private String businessNumber; // 사업자 번호
    private String vetImage; // 수의사 이미지 경로
    private String userRole; // 사용자 역할
    private String userSnsDist; // SNS 가입 여부
    private Integer userStatus; // 사용자 상태
    private String vetLicense; // 수의사 면허번호
}