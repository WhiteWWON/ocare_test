package com.ocare.Ocare.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 운영시 별도 설정값 사용
    private final long accessTokenTime = 30 * 60 * 1000L; // 30분

    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenTime))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email) {
        // Refresh Token은 보통 만료시간을 길게 설정 (Service에서 관리)
        return UUID.randomUUID().toString();
    }
}
