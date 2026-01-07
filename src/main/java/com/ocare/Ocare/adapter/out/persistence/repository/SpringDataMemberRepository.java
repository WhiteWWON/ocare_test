package com.ocare.Ocare.adapter.out.persistence.repository;

import com.ocare.Ocare.adapter.out.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataMemberRepository extends JpaRepository<MemberJpaEntity, Long> {
    boolean existsByEmail(String email);
    Optional<MemberJpaEntity> findByEmail(String email);
    //boolean existsByNickname(String nickname);

    @Modifying
    @Query("UPDATE MemberJpaEntity m SET m.login_fail_cnt = COALESCE(m.login_fail_cnt, 0) + 1, m.updated_dt = CURRENT_TIMESTAMP WHERE m.email = :email")
    void increaseLoginFailCount(@Param("email") String email);

    @Modifying
    @Query("UPDATE MemberJpaEntity m SET m.login_fail_cnt = 0, m.last_login_dt = CURRENT_TIMESTAMP, m.updated_dt = CURRENT_TIMESTAMP WHERE m.email = :email")
    void resetLoginFailCount(@Param("email") String email);
}

