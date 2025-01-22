package com.example.entity.DashboardEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 질병 랭킹 엔티티
 *
 * - 질병 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: disease_ranking
 * - 필드: diseaseId (질병 ID), diseaseName (질병 이름), casesCount (사례 수)
 */
@Data // Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Table("disease_ranking") // Cassandra 테이블 disease_ranking과 매핑
public class DiseaseRankingEntity {

    // Primary Key로 사용되는 질병 ID
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Column("disease_id") // Cassandra 컬럼 이름: disease_id
    private String diseaseId;

    // 질병 이름
    @Column("disease_name") // Cassandra 컬럼 이름: disease_name
    private String diseaseName;

    // 질병 발생 건수
    @Column("cases_count") // Cassandra 컬럼 이름: cases_count
    private int casesCount;

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "DiseaseRankingEntity{" +
                "diseaseId='" + diseaseId + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", casesCount=" + casesCount +
                '}';
    }
}
