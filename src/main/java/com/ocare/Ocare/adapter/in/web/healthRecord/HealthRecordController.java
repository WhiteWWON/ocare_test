package com.ocare.Ocare.adapter.in.web.healthRecord;

import com.ocare.Ocare.application.port.in.HealthRecordUseCase;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import com.ocare.Ocare.domain.model.RecordType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordUseCase healthRecordUseCase;

    @PostMapping("/bulk-record")
    public ResponseEntity<String> bulkRecord(@RequestBody HealthDataRequest request) {
        // 1. Master 정보 추출 (평탄화)
        HealthRecordMaster master = HealthRecordMaster.builder()
                .memberId(12345L) // 실제로는 세션이나 토큰에서 가져옴
                .recordKey(request.getRecordkey())
                .recordType(RecordType.fromJsonType(request.getType()).getCode()) // DB 저장용 코드로 변환
                .scMode(request.getData().getSource().getMode())
                .scProductName(request.getData().getSource().getProduct().getName())
                .scProductVender(request.getData().getSource().getProduct().getVender())
                .scAppName(request.getData().getSource().getName())
                .lastupdateDt(parseDateTime(request.getLastUpdate())) // 날짜 파싱 유틸 필요
                .createdId("WEB_TEST")
                .build();
        log.info("HealthRecordMaster 처리!");
        // 2. Detail 리스트 추출
        List<HealthRecordDetail> detailList = request.getData().getEntries().stream()
                .map(entry -> HealthRecordDetail.builder()
                        .steps(parseSteps(entry.getSteps()))
                        .periodFrom(parseISO8601(entry.getPeriod().getFrom()))
                        .periodTo(parseISO8601(entry.getPeriod().getTo()))
                        .distanceUnit(entry.getDistance().getUnit())
                        .distanceValue(entry.getDistance().getValue())
                        .caloriesUnit(entry.getCalories().getUnit())
                        .caloriesValue(entry.getCalories().getValue())
                        .createdId("WEB_TEST")
                        .build())
                .collect(Collectors.toList());
        log.info("HealthRecordDetail 처리!");
        healthRecordUseCase.recordBulkHealthData(master, detailList);

        return ResponseEntity.ok("Processed " + detailList.size() + " entries successfully.");
    }

    // 날짜 파싱용 간단 유틸 ("2024-11-14T23:10:00+0000" 포맷 대응)
    private LocalDateTime parseISO8601(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
    }
    private LocalDateTime parseDateTime(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"));
    }

    /**
     * steps 문자열을 BigDecimal로 변환
     */
    private BigDecimal parseSteps(String stepsStr) {
        if (stepsStr == null || stepsStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(stepsStr);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

}
