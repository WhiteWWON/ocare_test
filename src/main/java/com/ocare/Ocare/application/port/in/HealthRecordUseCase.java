package com.ocare.Ocare.application.port.in;

import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;

import java.util.List;

public interface HealthRecordUseCase {
    // 벌크 데이터 적재 유스케이스
    void recordBulkHealthData(HealthRecordMaster master, List<HealthRecordDetail> details);
}
