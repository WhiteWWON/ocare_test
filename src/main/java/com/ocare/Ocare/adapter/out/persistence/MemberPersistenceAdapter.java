package com.ocare.Ocare.adapter.out.persistence;

import com.ocare.Ocare.adapter.out.persistence.entity.MemberJpaEntity;
import com.ocare.Ocare.adapter.out.persistence.repository.SpringDataMemberRepository;
import com.ocare.Ocare.application.port.out.MemberPort;
import com.ocare.Ocare.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component // 핵심: 이 어노테이션이 있어야 'required a bean' 에러가 해결
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {
    private final SpringDataMemberRepository memberRepository;
    @Override
    public void saveMember(Member member) {
        // 1. 도메인 모델(Member)을 DB 엔티티(MemberJpaEntity)로 변환
        MemberJpaEntity jpaEntity = MemberJpaEntity.fromDomain(member);
        // 2. 실제 DB 저장 (Spring Data JPA 활용)
        memberRepository.save(jpaEntity);
    }
    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        // DB에서 조회 후 도메인 모델로 변환하여 반환 (로그인 시 사용)
        return memberRepository.findByEmail(email).map(MemberJpaEntity::toDomain);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseLoginFailCount(String email) {
        memberRepository.increaseLoginFailCount(email);
    }

    @Override
    public void resetLoginFailCount(String email) {
        memberRepository.resetLoginFailCount(email);
    }
}
