package com.ocare.Ocare.application.port.in;

import com.ocare.Ocare.application.port.in.dto.HealthRecordDetailDto;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class HealthRecordResponse {
    private Long rcMasterId;
    private String recordKey;
    private String recordType;
    private String memo;
    private Integer scMode;
    private String scProductName;
    private String scProductVender;
    private String scAppName;
    private Integer recordTotalCnt; // 처리된 누적 개수 (Checkpoint)
    private LocalDateTime lastupdateDt;
    private List<HealthRecordDetailDto> details;
}
