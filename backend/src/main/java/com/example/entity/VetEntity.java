package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;

@Getter
@Setter
@ToString
@Table("vet_info")
public class VetEntity {

     @PrimaryKey
     @Id
     @Column("vet_id")
     private UUID vetId;

     @Column("user_id") // 사용자 ID 추가
     private String userId;

     @Column("vet_image")
     private String vetImage;

     @Column("name")
     private String name;

     @Column("phone_number")
     private String phoneNumber;

     @Column("email")
     private String email;

     @Column("address")
     private String address;

     @Column("review_count")
     private int reviewCount;

     @Column("consultation_count")
     private int consultationCount;

     @Column("vet_rating")
     private float vetRating;

     // 새로운 필드 추가, 데이터베이스에 저장되지 않도록 transient 설정

     @Transient
     private double distance;
     @Transient
     private double latitude;
     @Transient
     private double longitude;


     // 기본 생성자 추가
     public VetEntity(){}

     // distance 포함하는 생성자 추가
     public VetEntity(UUID vetId, String userId, String vetImage, String name, String phoneNumber, String email, String address,
                      int reviewCount, int consultationCount, float vetRating, double latitude, double longitude, double distance) {
          this.vetId = vetId;
          this.userId = userId;
          this.vetImage = vetImage;
          this.name = name;
          this.phoneNumber = phoneNumber;
          this.email = email;
          this.address = address;
          this.reviewCount = reviewCount;
          this.consultationCount = consultationCount;
          this.vetRating = vetRating;
          this.latitude = latitude;
          this.longitude = longitude;
          this.distance = distance;
     }
}