package com.example.repository;

import com.example.entity.UserTokenEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends CassandraRepository<UserTokenEntity, String> {
    // 기본적으로 CassandraRepository를 사용하여 findById, save 등의 메서드 자동 생성
}
