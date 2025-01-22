package com.example.entity.DashboardEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 제조사 랭킹 엔티티
 *
 * - 제조사 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: origin_ranking
 * - 필드: originId (제조사 ID), originName (제조사 이름), totalSales (총 판매량)
 */
@Data // Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Table("origin_ranking") // Cassandra 테이블 origin_ranking과 매핑
public class OriginRankingEntity {

    // Primary Key로 사용되는 제조사 ID
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Column("origin_id") // Cassandra 컬럼 이름: origin_id
    private String originId;

    // 제조사 이름
    @Column("origin_name") // Cassandra 컬럼 이름: origin_name
    private String originName;

    // 제조사 총 판매량
    @Column("origin_sales") // Cassandra 컬럼 이름: origin_sales
    private int originSales;

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "OriginRankingEntity{" +
                "originId='" + originId + '\'' +
                ", originName='" + originName + '\'' +
                ", originSales=" + originSales +
                '}';
    }
}
