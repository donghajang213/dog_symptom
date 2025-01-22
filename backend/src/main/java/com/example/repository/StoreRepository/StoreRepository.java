package com.example.repository.StoreRepository;

import com.example.entity.StoreEntity.StoreEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends CassandraRepository<StoreEntity, String> {
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 작성
}
