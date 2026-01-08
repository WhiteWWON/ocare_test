package com.ocare.Ocare.application.service;

import com.ocare.Ocare.application.port.in.HealthRecordUseCase;
import com.ocare.Ocare.application.port.out.HealthRecordPort;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthRecordService implements HealthRecordUseCase {
    private final HealthRecordPort healthRecordPort;

    @Override
    @Transactional
    public void recordBulkHealthData(HealthRecordMaster master, List<HealthRecordDetail> details) {
        if (details == null || details.isEmpty()) {
            log.info("처리할 데이터가 없습니다. RecordKey: {}", master.getRecordKey());
            return;
        }
        // 1. 마스터 데이터 Upsert (기존 ID 채번)
        healthRecordPort.saveMaster(master);
        Long masterId = master.getRcMasterId();
        log.info("[START] Bulk processing for MasterId: {}, MemberId: {}, Total: {}", masterId, master.getMemberId(), details.size());

        // 2. 이전에 성공했던 기록 확인
        int savedCount = healthRecordPort.getProcessedCount(masterId);

        // [핵심] 순서가 바뀌어 들어올 경우를 대비해 100건 정도 뒤로 물러나서(Overlap) 시작
        int startOffset = Math.max(0, savedCount - 100);
        // Offset이 리스트 크기보다 크면 처리할 게 없음
        if (startOffset >= details.size()) {
            log.info("[SKIP] All data already processed. MasterId: {}", masterId);
            return;
        }
        List<HealthRecordDetail> processList = details.subList(startOffset, details.size());

        // 3. 데이터 매핑 및 벌크 저장
        final int BATCH_SIZE = 1000;
        try {
            for (int i = 0; i < processList.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, processList.size());
                List<HealthRecordDetail> chunk = processList.subList(i, endIndex);

                chunk.forEach(d -> {
                    d.setRcMasterId(masterId);
                    d.setMemberId(master.getMemberId());
                });

                // ON DUPLICATE KEY UPDATE를 활용한 안전한 적재
                healthRecordPort.upsertDetails(chunk);

                // 4. 체크포인트 업데이트 (절대적 위치 기록)
                int currentTotal = startOffset + i + chunk.size();
                healthRecordPort.updateProcessedCount(masterId, currentTotal);
                log.debug("[PROGRESS] MasterId: {}, Saved: {}/{}", masterId, currentTotal, details.size());
            }
            log.info("[SUCCESS] Processing completed for MasterId: {}", masterId);
        } catch (Exception e) {
            log.error("[ERROR] Bulk processing failed for MasterId: {}. Error: {}", masterId, e.getMessage());
            throw e; // 트랜잭션 롤백
        }
    }
}
