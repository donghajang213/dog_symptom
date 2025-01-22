package com.example.entity.DashboardEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 품종 랭킹 엔티티
 *
 * - 품종 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: breed_ranking
 * - 필드: breedId (품종 ID), breedName (품종 이름), casesCount (건수)
 */
@Data // Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Table("breed_ranking") // Cassandra 테이블 breed_ranking과 매핑
public class BreedRankingEntity {

    // Primary Key로 사용되는 품종 ID
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Id // Spring Data용 ID 어노테이션
    @Column("breed_id") // Cassandra 컬럼 이름: breed_id
    private String breedId;

    // 품종 이름
    @Column("dog_category") // Cassandra 컬럼 이름: dog_category
    private String dogCategory;

    // 품종 건수
    @Column("cases_count") // Cassandra 컬럼 이름: cases_count
    private int casesCount;

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "BreedRankingEntity{" +
                "breedId='" + breedId + '\'' +
                ", dogCategory='" + dogCategory + '\'' +
                ", casesCount=" + casesCount +
                '}';
    }
}
