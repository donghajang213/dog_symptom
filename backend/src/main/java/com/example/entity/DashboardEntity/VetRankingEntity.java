package com.example.entity.DashboardEntity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 수의사 랭킹 엔티티
 *
 * - 수의사 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: vet_ranking
 * - 필드: vet_id, vet_name, vet_rating
 */
@Getter // getter 메서드 자동 생성
@Setter // setter 메서드 자동 생성
@Data // @ToString, @EqualsAndHashCode 등을 포함한 Lombok 어노테이션
@NoArgsConstructor // 기본 생성자 자동 생성
@Table("vet_ranking") // Cassandra 테이블 vet_ranking과 매핑
public class VetRankingEntity {

    // Primary Key로 사용되는 ID 필드
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Id // Spring Data용 ID 어노테이션
    @Column("vet_id") // Cassandra 컬럼 이름: vet_id
    private String vetId;

    // 수의사 이름
    @Column("vet_name") // Cassandra 컬럼 이름: vet_name
    private String vetName;

    // 수의사 평점 (소수점 포함)
    @Column("vet_rating") // Cassandra 컬럼 이름: vet_rating
    private double vetRating;

//    /**
//     * 매개변수를 받는 생성자
//     *
//     * @param vetId    고유 ID
//     * @param vetName  수의사 이름
//     * @param vetRating 수의사 평점
//     */
//    public VetRankingEntity(String vetId, String vetName, double vetRating) {
//        this.vetId = vetId;
//        this.vetName = vetName;
//        this.vetRating = vetRating;
//    }

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "VetRankingEntity{" +
                "vetId='" + vetId + '\'' +
                ", vetName='" + vetName + '\'' +
                ", vetRating=" + vetRating +
                '}';
    }
}
