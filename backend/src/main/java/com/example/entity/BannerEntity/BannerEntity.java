package com.example.entity.BannerEntity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("banners") // 카산드라 테이블 이름
public class BannerEntity {
    @PrimaryKey
    @Column("banner_id")
    private UUID bannerId; // 배너 고유 ID

    @Column("image_url")
    private String imageUrl; // 배너 이미지 URL

    @Column("redirect_url")
    private String redirectUrl; // 배너 클릭 시 이동할 URL

    @Column("priority")
    private int priority; // 배너 우선순위

    @Column("start_date")
    private LocalDate startDate; // 배너 시작 날짜

    @Column("end_date")
    private LocalDate endDate; // 배너 종료 날짜
}
