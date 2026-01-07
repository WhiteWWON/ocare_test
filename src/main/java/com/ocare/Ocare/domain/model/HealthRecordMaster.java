package com.ocare.Ocare.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordMaster {
    private Long rcMasterId;
    private Long memberId;
    private String recordKey;
    private String recordType; // S, D, C 등
    private Integer scMode;
    private String scProductName;
    private String scProductVender;
    private String scAppName;
    private Integer recordTotalCnt; // 처리된 누적 개수 (Checkpoint)
    private LocalDateTime lastupdateDt;
    private String createdId;
}
