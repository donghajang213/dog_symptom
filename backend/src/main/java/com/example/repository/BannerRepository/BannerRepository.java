package com.example.repository.BannerRepository;

import com.example.entity.BannerEntity.BannerEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface BannerRepository extends CassandraRepository<BannerEntity, UUID> {
    // 기본 CRUD 메서드 제공 (findAll, save, deleteById 등)
    //    // 특정 컬럼만 삽입하는 CQL 쿼리
//    @Query("INSERT INTO banners (banner_id, image_url, redirect_url, priority, start_date, end_date) " +
//            "VALUES (:bannerId, :imageUrl, :redirectUrl, :priority, :startDate, :endDate)")
//    void insertBanner(UUID bannerId, String imageUrl, String redirectUrl, int priority, Date startDate, Date endDate);
}
