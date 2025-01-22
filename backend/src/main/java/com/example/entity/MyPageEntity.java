package com.example.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Data
@Table("mypage")
public class MyPageEntity {
    @PrimaryKey
    @Id
    @Column("user_id") // 사용자 ID
    private String userId;

    @Column("pet_id") // 반려동물 ID
    private String petId;

    @Column("user_name") // 사용자 이름
    private String userName;

    @Column("email") // 사용자 이메일
    private String email;

    @Column("pet_name") // 반려동물 이름
    private String petName;

    public MyPageEntity() {}

    public MyPageEntity(String userId, String petId, String userName, String email, String petName) {
        this.userId = userId;
        this.petId = petId;
        this.userName = userName;
        this.email = email;
        this.petName = petName;
    }

    @Override
    public String toString() {
        return "MyPageEntity{" +
                "userId='" + userId + '\'' +
                ", petId='" + petId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", petName='" + petName + '\'' +
                '}';
    }
}
