package com.ocare.Ocare.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisAuthAdapter implements AuthTokenPort {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveRefreshToken(String email, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set(
                "RT:" + email,
                refreshToken,
                Duration.ofMillis(expirationTime)
        );
    }

    @Override
    public boolean validateRefreshToken(String email, String refreshToken) {
        String savedToken = redisTemplate.opsForValue().get("RT:" + email);
        return refreshToken.equals(savedToken);
    }
    @Override
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:" + email);
    }
    @Override
    public void reportBlacklist(String accessToken, long remainingTime) {
        // 키 앞에 "BL:"을 붙여 블랙리스트임을 구분하고, 토큰의 남은 수명만큼만 저장
        redisTemplate.opsForValue().set(
                "BL:" + accessToken,
                "logout",
                Duration.ofMillis(remainingTime)
        );
    }
    @Override
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + accessToken));
    }
}
