package com.example.entity.DashboardEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * 상품 랭킹 엔티티
 *
 * - 상품 랭킹 정보를 저장하는 Cassandra 테이블과 매핑된 클래스
 * - 테이블 이름: product_ranking
 * - 필드: productId (상품 ID), productName (상품 이름), salesCount (판매 수량)
 */
@Data // Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Table("product_ranking") // Cassandra 테이블 product_ranking과 매핑
public class ProductRankingEntity {

    // Primary Key로 사용되는 상품 ID
    @PrimaryKey // Cassandra 테이블의 기본 키
    @Column("product_id") // Cassandra 컬럼 이름: product_id
    private String productId;

    // 상품 이름
    @Column("product_name") // Cassandra 컬럼 이름: product_name
    private String productName;

    // 상품 판매 수량
    @Column("product_sales") // Cassandra 컬럼 이름: product_sales
    private int productSales;

    /**
     * 객체 정보를 출력하기 위한 toString 메서드
     *
     * @return 객체의 필드 값을 포함한 문자열
     */
    @Override
    public String toString() {
        return "ProductRankingEntity{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productSales=" + productSales +
                '}';
    }
}
