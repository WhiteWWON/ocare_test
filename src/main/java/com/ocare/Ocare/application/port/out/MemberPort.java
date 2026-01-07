package com.ocare.Ocare.application.port.out;

import com.ocare.Ocare.domain.model.Member;

import java.util.Optional;

public interface MemberPort {
    void saveMember(Member member);
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    void increaseLoginFailCount(String email); // login_fail_cnt + 1
    void resetLoginFailCount(String email);    // login_fail_cnt = 0, last_login_dt = NOW()
}
