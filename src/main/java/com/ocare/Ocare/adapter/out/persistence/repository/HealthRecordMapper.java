package com.ocare.Ocare.adapter.out.persistence.repository;

import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.Optional;

@Mapper
@MapperScan
public interface HealthRecordMapper {
    // record_key 중복 시 rc_master_id를 반환받기 위한 Upsert형 Insert
    int insertMaster(HealthRecordMaster master);

    // 상세 데이터 벌크 Upsert
    int upsertDetailBatch(@Param("details") List<HealthRecordDetail> details);

    // 진행률 확인 및 업데이트
    int getProcessedCount(@Param("rcMasterId") Long rcMasterId);
    void updateProcessedCount(@Param("rcMasterId") Long rcMasterId, @Param("processedCnt") int processedCnt, @Param("createdId") String createdId);
    Optional<HealthRecordMaster> selectMasterByRecordKey(@Param("recordKey") String recordKey, @Param("memberId") Long memberId);
}
