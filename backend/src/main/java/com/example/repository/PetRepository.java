package com.example.repository;

import com.example.entity.PetEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PetRepository extends CassandraRepository<PetEntity, UUID> {

    // 사용자 ID로 반려동물 조회 (ALLOW FILTERING 사용)
    @Query("SELECT * FROM pets WHERE user_id = ?0")
    List<PetEntity> findByUserId(String userId);

    // user_id와 pet_id로 pet 삭제
    @Query("DELETE FROM pets WHERE user_id = ?0")
    void deleteAllByUserId(String userId);

    @Query("DELETE FROM pets WHERE user_id = ?0 AND pet_id = ?1")
    void deleteByUserIdAndPetId(String userId, UUID petId);

    @Query("SELECT * FROM pets WHERE user_id = ?0 AND pet_id = ?1")
    Optional<PetEntity> findByUserIdAndPetId(String userId, UUID petId);

    // petId를 통해 PetEntity 조회
    PetEntity findByPetId(UUID petId);

}