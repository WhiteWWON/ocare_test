package com.ocare.Ocare.application.service;

import com.ocare.Ocare.adapter.out.redis.AuthTokenPort;
import com.ocare.Ocare.application.port.in.LoginCommand;
import com.ocare.Ocare.application.port.in.LoginUseCase;
import com.ocare.Ocare.application.port.in.SignUpCommand;
import com.ocare.Ocare.application.port.in.SignUpUseCase;
import com.ocare.Ocare.application.port.out.MemberPort;
import com.ocare.Ocare.domain.model.AuthToken;
import com.ocare.Ocare.domain.model.Member;
import com.ocare.Ocare.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements SignUpUseCase, LoginUseCase {

    private final MemberPort memberPort;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthTokenPort authTokenPort; // Redis 포트
    private final JwtTokenProvider jwtTokenProvider;

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

    @Override
    @Transactional
    public AuthToken login(LoginCommand command) {
        // 1. 사용자 확인
        Member member = memberPort.findByEmail(command.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // 4. Redis에 Refresh Token 저장 (예: 7일간 유효)
        authTokenPort.saveRefreshToken(member.getEmail(), refreshToken, 7 * 24 * 60 * 60 * 1000L);

        return new AuthToken(accessToken, refreshToken);
    }
}
