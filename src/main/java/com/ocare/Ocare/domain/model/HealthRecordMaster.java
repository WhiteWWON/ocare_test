package com.ocare.Ocare.domain.model;

import com.ocare.Ocare.application.port.in.dto.HealthRecordDetailDto;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "OC_HEALTH_RECORD_MASTER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<HealthRecordDetail> details = new ArrayList<>();

    public List<HealthRecordDetailDto> toDetailDtos() {
        if (this.details == null) return Collections.emptyList();
        return this.details.stream()
                .map(d -> HealthRecordDetailDto.builder()
                        .rcDetailId(d.getRcDetailId())
                        .steps(d.getSteps().doubleValue())
                        .periodFrom(d.getPeriodFrom())
                        .periodTo(d.getPeriodTo())
                        .distanceValue(d.getDistanceValue())
                        .distanceUnit(d.getDistanceUnit())
                        .caloriesValue(d.getCaloriesValue())
                        .caloriesUnit(d.getCaloriesUnit())
                        .build())
                .collect(Collectors.toList());
    }
}
