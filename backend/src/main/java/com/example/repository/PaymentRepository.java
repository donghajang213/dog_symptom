package com.example.repository;

import com.example.entity.PaymentEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CassandraRepository<PaymentEntity, String> {
    // 추가 쿼리가 필요한 경우 아래에 정의 가능
}
