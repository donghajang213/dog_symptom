package com.example.entity.EventEntity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("events")
public class EventEntity {
    @PrimaryKey
    @Column("event_id")
    private UUID eventId; // UUID 형태

    @Column("event_title")
    private String eventTitle;

    @Column("event_desc")
    private String eventDesc;

    @Column("image_url")
    private String imageUrl;

    @Column("start_date")
    private LocalDate startDate; // 날짜 문자열 (YYYY-MM-DD)

    @Column("end_date")
    private LocalDate endDate;

    @Column("status")
    private String status; // 진행 상태 (예: 진행 중, 종료됨 )
}
