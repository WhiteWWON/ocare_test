package com.ocare.Ocare.adapter.out.redis;

public interface AuthTokenPort {
    void saveRefreshToken(String email, String refreshToken, long expirationTime);
    boolean validateRefreshToken(String email, String refreshToken);
    void deleteRefreshToken(String email);
    // 블랙리스트 관련 추가
    void reportBlacklist(String accessToken, long remainingTime);
    boolean isBlacklisted(String accessToken);
}
