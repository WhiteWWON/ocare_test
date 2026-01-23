package com.ocare.Ocare.application.port.in.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Builder
@AllArgsConstructor
public class HealthRecordDetailDto {
    private Long rcDetailId;
    private BigDecimal steps;
    private LocalDateTime periodFrom;
    private LocalDateTime periodTo;
    private BigDecimal distanceValue;
    private String distanceUnit;
    private BigDecimal caloriesValue;
    private String caloriesUnit;
    // 여기에는 Master 필드를 절대 넣지 않는다. (무한 루프 차단 핵심)
}
