package com.ocare.Ocare.adapter.in.web.healthRecord;

import com.ocare.Ocare.application.port.in.HealthRecordUseCase;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import com.ocare.Ocare.domain.model.RecordType;
import com.ocare.Ocare.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/bulk-record")
    public ResponseEntity<String> bulkRecord(@RequestBody HealthDataRequest request, @RequestHeader("Authorization") String authHeader // "Bearer [token]" 형태로 들어옴
    ) {
        // 1. 토큰에서 실제 memberId 추출
        Long currentMemberId = jwtTokenProvider.getMemberId(authHeader);
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
        
        // 2. HealthRecordMaster 정보 설정
        HealthRecordMaster master = HealthRecordMaster.builder()
                .memberId(currentMemberId) // 토큰에서 가져온 member_id
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

        // 3. Detail 리스트 추출
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
