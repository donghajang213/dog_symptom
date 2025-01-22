package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Table("users")
public class UserEntity {

    @PrimaryKey
    @Id
    @Column("user_id") // Cassandra 컬럼 이름과 매핑
    private String userId;

    @Column("address")
    private String address;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("detailed_address")
    private String detailedAddress;

    @Column("email")
    private String email;

    @Column("gender")
    private String gender;

    @Column("name")
    private String name;

    @Column("password")
    private String password;

    @Column("phone_number")
    private String phoneNumber;

    @Column("bank_account") // 계좌번호
    private String bankAccount;

    @Column("business_number") // 사업자 번호
    private String businessNumber;

    @Column("vet_image") // 수의사 이미지 경로
    private String vetImage;

    @Column("user_regdate") // 가입일
    private LocalDateTime userRegdate;

    @Column("user_role") // 사용자 역할
    private String userRole;

    @Column("user_sns_dist") // SNS 가입 여부
    private String userSnsDist;

    @Column("user_status") // 사용자 상태
    private Integer userStatus;

    @Column("vet_license") // 수의사 면허번호
    private String vetLicense;

    public UserEntity() {
    }

    public UserEntity(String userId, String address, LocalDate birthDate, String detailedAddress, String email,
                      String gender, String name, String password, String phoneNumber, String bankAccount,
                      String businessNumber, String vetImage, LocalDateTime userRegdate, String userRole,
                      String userSnsDist, Integer userStatus, String vetLicense) {
        this.userId = userId;
        this.address = address;
        this.birthDate = birthDate;
        this.detailedAddress = detailedAddress;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.bankAccount = bankAccount;
        this.businessNumber = businessNumber;
        this.vetImage = vetImage;
        this.userRegdate = userRegdate;
        this.userRole = userRole;
        this.userSnsDist = userSnsDist;
        this.userStatus = userStatus;
        this.vetLicense = vetLicense;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId='" + userId + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                ", detailedAddress='" + detailedAddress + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", businessNumber='" + businessNumber + '\'' +
                ", vetImage='" + vetImage + '\'' +
                ", userRegdate=" + userRegdate +
                ", userRole='" + userRole + '\'' +
                ", userSnsDist='" + userSnsDist + '\'' +
                ", userStatus=" + userStatus +
                ", vetLicense='" + vetLicense + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
            this.password = password;
        }

}
