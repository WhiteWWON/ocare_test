package com.ocare.Ocare.adapter.out.persistence.repository;

import com.ocare.Ocare.adapter.out.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataMemberRepository extends JpaRepository<MemberJpaEntity, Long> {
    boolean existsByEmail(String email);
    Optional<MemberJpaEntity> findByEmail(String email);
    //boolean existsByNickname(String nickname);
}

