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
}
