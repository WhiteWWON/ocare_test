package com.ocare.Ocare.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

// domain/model/HealthRecordDetail.java
@Entity
@Table(name = "OC_HEALTH_RECORD_DETAIL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rcDetailId;

    @Column(name = "rc_master_id", insertable = false, updatable = false)
    private Long rcMasterId; // PK 및 파티션 키
    private Long memberId;
    private BigDecimal steps;
    private LocalDateTime periodFrom; // PK 및 파티션 키
    private LocalDateTime periodTo;
    private String distanceUnit;
    private BigDecimal distanceValue;
    private String caloriesUnit;
    private BigDecimal caloriesValue;
    private String createdId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rc_master_id", nullable = false)
    private HealthRecordMaster master;
}
