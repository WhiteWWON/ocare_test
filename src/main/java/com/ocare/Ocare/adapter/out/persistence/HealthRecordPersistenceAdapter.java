package com.ocare.Ocare.adapter.out.persistence;

import com.ocare.Ocare.adapter.out.persistence.repository.HealthRecordMapper;
import com.ocare.Ocare.application.port.out.HealthRecordPort;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HealthRecordPersistenceAdapter implements HealthRecordPort {
    private final HealthRecordMapper healthRecordMapper;

    @Override
    public void saveMaster(HealthRecordMaster master) {
        healthRecordMapper.insertMaster(master);
    }

    @Override
    public void upsertDetails(List<HealthRecordDetail> details) {
        // 네트워크 패킷 제한을 고려하여 1,000건씩 분할 전송
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < details.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, details.size());
            healthRecordMapper.upsertDetailBatch(details.subList(i, endIndex));
        }
    }

    @Override
    public int getProcessedCount(Long rcMasterId) {
        return healthRecordMapper.getProcessedCount(rcMasterId);
    }

    @Override
    public void updateProcessedCount(Long rcMasterId, int processedCnt, String createdId) {
        healthRecordMapper.updateProcessedCount(rcMasterId, processedCnt, createdId);
    }

    @Override
    public Optional<HealthRecordMaster> findMasterByRecordKey(String recordKey, Long memberId) {
        return healthRecordMapper.selectMasterByRecordKey(recordKey, memberId);
    }
}
