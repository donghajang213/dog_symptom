package com.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("payment")
public class PaymentEntity {

    @PrimaryKey
    @Id
    @Column("order_id")
    private String orderId; // UUID로 생성된 주문 ID

    @Column("order_name")
    private String orderName; // 상품 이름

    @Column("amount")
    private Integer amount; // 결제 금액

    @Column("payment_key")
    private String paymentKey; // 결제 승인 키

    @Column("status")
    private String status; // 결제 상태 (SUCCESS, FAILED 등)

    @Column("user_id")
    private String userId; // 사용자 ID (세션 정보)

    @Column("user_name")
    private String userName; // 사용자 이름 (세션 정보)

    @Column("created_at")
    private LocalDateTime createdAt; // 결제 요청 시점
}
