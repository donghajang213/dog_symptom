package com.example.entity.DashboardEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 회원가입 랭킹 엔티티
 *
 * - 회원가입 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: signup_ranking
 * - 필드: userId (회원 ID), month (가입 월), signupCount (회원가입 수)
 */
@Data // Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Table("signup_ranking") // Cassandra 테이블 signup_ranking과 매핑
public class SignupRankingEntity {

    // Primary Key로 사용되는 월별 정보
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Column("month") // Cassandra 컬럼 이름: month
    private String month;

    // 회원 아이디
    @Column("user_id")
    private String userId;

    // 월별 회원가입 수
    @Column("signup_count") // Cassandra 컬럼 이름: signup_count
    private int signupCount;

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "SignupRankingEntity{" +
                "month='" + month + '\'' +
                "userId=" + userId + '\'' +
                ", signupCount=" + signupCount +
                '}';
    }
}
