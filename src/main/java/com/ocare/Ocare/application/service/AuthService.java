package com.ocare.Ocare.application.service;

import com.ocare.Ocare.application.port.in.SignUpCommand;
import com.ocare.Ocare.application.port.in.SignUpUseCase;
import com.ocare.Ocare.application.port.out.MemberPort;
import com.ocare.Ocare.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements SignUpUseCase {

    private final MemberPort memberPort;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpCommand command) {
        if (memberPort.existsByEmail(command.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .name(command.getName())
                .nickname(command.getNickname())
                .loginCnt(0)
                .createdId("SYSTEM")
                .build();

        memberPort.saveMember(member);
    }
}
