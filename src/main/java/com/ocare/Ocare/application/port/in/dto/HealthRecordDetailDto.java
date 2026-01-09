package com.ocare.Ocare.application.port.in.dto;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Builder
@AllArgsConstructor
public class HealthRecordDetailDto {
    private Long rcDetailId;
    private Double steps;
    private LocalDateTime periodFrom;
    private LocalDateTime periodTo;
    private Double distanceValue;
    private String distanceUnit;
    private Double caloriesValue;
    private String caloriesUnit;
    // 여기에는 Master 필드를 절대 넣지 않는다. (무한 루프 차단 핵심)
}
