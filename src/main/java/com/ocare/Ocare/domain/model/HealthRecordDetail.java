package com.ocare.Ocare.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordDetail {
    private Long rcDetailId;
    private Long rcMasterId;
    private Long memberId;
    private BigDecimal steps;
    private LocalDateTime periodFrom; // PK 및 파티션 키
    private LocalDateTime periodTo;
    private String distanceUnit;
    private Double distanceValue;
    private String caloriesUnit;
    private Double caloriesValue;
    private String createdId;
}
