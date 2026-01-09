package com.ocare.Ocare.adapter.out.persistence;

import com.ocare.Ocare.adapter.out.persistence.repository.HealthRecordMapper;
import com.ocare.Ocare.application.port.out.HealthRecordPort;
import com.ocare.Ocare.domain.model.HealthRecordDetail;
import com.ocare.Ocare.domain.model.HealthRecordMaster;
import com.ocare.Ocare.domain.model.QHealthRecordDetail;
import com.ocare.Ocare.domain.model.QHealthRecordMaster;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Component
@Repository
@RequiredArgsConstructor
public class HealthRecordPersistenceAdapter implements HealthRecordPort {
    private final HealthRecordMapper healthRecordMapper;
    private final JPAQueryFactory queryFactory;
    private final QHealthRecordMaster master = QHealthRecordMaster.healthRecordMaster;
    private final QHealthRecordDetail detail = QHealthRecordDetail.healthRecordDetail;

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

    @Override
    public Page<HealthRecordMaster> findAllByMemberWithDetails(Long memberId, Pageable pageable) {

        // 1. 데이터 조회 (Fetch Join 활용)
        List<HealthRecordMaster> content = queryFactory
                .selectFrom(master)
                .leftJoin(master.details, detail).fetchJoin() // 일대다 조인
                .where(master.memberId.eq(memberId))
                .orderBy(master.lastupdateDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(master.count())
                .from(master)
                .where(master.memberId.eq(memberId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
