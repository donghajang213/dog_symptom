package com.example.repository.DashboardRepository;

import com.example.entity.UserEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignupRankingRepository extends CassandraRepository<UserEntity, String> {

    // 모든 유저 데이터 조회 (가입일 기준으로 월별로 집계는 애플리케이션에서 처리)
    List<UserEntity> findAll();
}