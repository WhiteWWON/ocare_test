package com.ocare.Ocare.adapter.out.redis;

public interface AuthTokenPort {
    void saveRefreshToken(String email, String refreshToken, long expirationTime);
    boolean validateRefreshToken(String email, String refreshToken);
    void deleteRefreshToken(String email);
}
