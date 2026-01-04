package com.ocare.Ocare.adapter.in.web.auth;

import com.ocare.Ocare.domain.model.AuthToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 생성자 접근권한 private로 설정
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    // 도메인 모델을 응답 DTO로 변환하는 정적 팩토리 메서드
    public static AuthTokenResponse from(AuthToken token) {
        return new AuthTokenResponse(
                token.getAccessToken(),
                token.getRefreshToken(),
                "Bearer"
        );
    }
}
