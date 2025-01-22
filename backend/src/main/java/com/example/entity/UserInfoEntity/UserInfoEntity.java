package com.example.entity.UserInfoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserInfoEntity {

    @PrimaryKey
    @Column("user_id") // 테이블의 user_id 컬럼과 매핑
    private String userId;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("detailed_address")
    private String detailedAddress;

    @Column("user_role") // 역할 (일반 회원, 수의사 등)
    private String userRole;

    @Column("user_regdate")
    private Timestamp userRegdate;

    @Column("bank_account")
    private String bankAccount;

    @Column("birth_date")
    private LocalDate birthDate;

}

