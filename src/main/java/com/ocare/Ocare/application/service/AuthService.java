package com.ocare.Ocare.application.service;

import com.ocare.Ocare.adapter.out.redis.AuthTokenPort;
import com.ocare.Ocare.application.port.in.*;
import com.ocare.Ocare.application.port.out.MemberPort;
import com.ocare.Ocare.domain.model.AuthToken;
import com.ocare.Ocare.domain.model.Member;
import com.ocare.Ocare.global.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService implements SignUpUseCase, LoginUseCase, LogoutUseCase {

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
                .loginFailCnt(0)
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

        // 2. 계정 잠금 여부 확인 (예: 로그인 5회 이상 실패 시)
        if (member.getLoginFailCnt() != null && member.getLoginFailCnt() >= 5) {
            throw new RuntimeException("비밀번호 5회 오류로 인해 계정이 잠겼습니다. 관리자에게 문의하세요.");
        }

        // 3. 비밀번호 일치 확인
        if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
            // 실패 시 카운트 증가 (Port를 통해 DB 업데이트)
            memberPort.increaseLoginFailCount(command.getEmail());
            int updatedFailCnt = (member.getLoginFailCnt() == null ? 0 : member.getLoginFailCnt()) + 1;
            throw new RuntimeException("비밀번호가 일치하지 않습니다. (현재 실패 횟수: " + updatedFailCnt + "/5)");
        }

        // 4. 로그인 성공 시 처리
        // 실패 카운트 초기화 및 마지막 로그인 시간 업데이트
        memberPort.resetLoginFailCount(command.getEmail());

        // 5. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // 6. Redis에 Refresh Token 저장 (예: 7일간 유효)
        authTokenPort.saveRefreshToken(member.getEmail(), refreshToken, 7 * 24 * 60 * 60 * 1000L);

        return new AuthToken(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(String email, String accessToken) {
        // 1/ Redis에서 해당 이메일로 저장된 Refresh Token 삭제
        authTokenPort.deleteRefreshToken(email);

        // 2. AccessToken 남은 유효시간 계산
        long expiration = jwtTokenProvider.getExpiration(accessToken); // 아래 유틸에 추가 필요
        long now = new Date().getTime();
        long remainingTime = expiration - now;

        // 3. AccessToken 블랙리스트로 등록
        if (remainingTime > 0) {
            authTokenPort.reportBlacklist(accessToken, remainingTime);
        }
    }
}
