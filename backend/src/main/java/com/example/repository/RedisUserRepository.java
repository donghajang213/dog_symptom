package com.example.repository;

import com.example.entity.RedisUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<RedisUserEntity, String> {
    // 기본적인 CRUD 기능은 CrudRepository에서 제공됩니다.
    // 필요 시 커스텀 메서드 추가 가능
}
