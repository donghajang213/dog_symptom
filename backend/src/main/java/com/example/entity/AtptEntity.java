package com.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("atpt") // 테이블 이름
public class AtptEntity {

    @Id
    @Column("atpt_id")
    private UUID atptId; // 대화 고유 ID

    @Column("user_id")
    private String userId; // 사용자 ID

    @Column("atpt_input")
    private String atptInput; // 사용자 입력

    @Column("atpt_output")
    private String atptOutput; // GPT 응답

    @Column("atpt_time")
    private LocalDate atptTime; // 대화 시간
}
