package com.ocare.Ocare.application.port.out;

import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;

import java.util.List;
import java.util.Optional;

public interface HealthRecordPort {

    // 마스터 데이터 저장 및 ID 채번
    void saveMaster(HealthRecordMaster master);

    // 상세 데이터 벌크 Upsert (ON DUPLICATE KEY UPDATE)
    void upsertDetails(List<HealthRecordDetail> details);

    // 마스터 ID 기준 현재까지 처리된 상세 레코드 개수 조회
    int getProcessedCount(Long rcMasterId);

    // 마스터 ID 기준 진행 상황(Checkpoint) 업데이트
    void updateProcessedCount(Long rcMasterId, int processedCnt, String createdId);

    // [추가] recordKey와 memberId로 기존 마스터 존재 여부 확인 (멱등성 보장용)
    Optional<HealthRecordMaster> findMasterByRecordKey(String recordKey, Long memberId);
}
