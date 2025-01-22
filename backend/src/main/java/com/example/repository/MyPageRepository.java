package com.example.repository;

import com.example.entity.MyPageEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyPageRepository extends CassandraRepository<MyPageEntity, String> {
    // 사용자 ID로 데이터 조회
    List<MyPageEntity> findByUserId(String userId);

    // 사용자 ID와 반려동물 ID로 특정 데이터 조회
    MyPageEntity findByUserIdAndPetId(String userId, String petId);
}
