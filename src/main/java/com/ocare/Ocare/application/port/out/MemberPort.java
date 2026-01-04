package com.ocare.Ocare.application.port.out;

import com.ocare.Ocare.domain.model.Member;

import java.util.Optional;

public interface MemberPort {
    void saveMember(Member member);
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}
