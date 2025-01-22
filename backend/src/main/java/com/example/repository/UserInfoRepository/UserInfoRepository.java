package com.example.repository.UserInfoRepository;

import com.example.entity.UserInfoEntity.UserInfoEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends CassandraRepository<UserInfoEntity, String> {
    // 이름, 아이디, 역할을 동시에 검색
    @Query("SELECT * FROM users WHERE name CONTAINS :keyword OR user_id CONTAINS :keyword OR user_role CONTAINS :keyword ALLOW FILTERING")
    List<UserInfoEntity> searchByKeyword(String keyword);
}
