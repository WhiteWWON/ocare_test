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
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 암호화 키(HS256 사용)
    private final long accessTokenTime = 30 * 60 * 1000L; // Access Token의 유효 시간 설정 (30분)

    /**
     * Access Token 생성 메서드
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder() // JWT 생성 시작
                .setSubject(email) // 토큰 주인(이메일)설정
                .setIssuedAt(now)  // 발행 시간 설정
                .setExpiration(new Date(now.getTime() + accessTokenTime)) //만료 시간 설정
                .signWith(key) // 비밀 키로 서명
                .compact(); // 최종 문자열로 압축
    }

    public String createRefreshToken(String email) {
        // (1) UUID를 사용하여 중복되지 않는 고유한 랜덤 문자열 생성
        // (2) Refresh Token은 보통 만료시간을 길게 설정 (Service에서 관리)
        return UUID.randomUUID().toString();
    }

    /**
     * 토큰에서 사용자 정보(이메일) 추출
     */
    public String getEmail(String token) {
        return Jwts.parserBuilder() // 토큰 해석기 준비
                .setSigningKey(key) // 검증에 쓸 키 설정
                .build() // 빌드
                .parseClaimsJws(token)// 토큰 분석 및 검증
                .getBody() // 내용물(Claims) 가져오기
                .getSubject(); // 이메일 반환
    }

    /**
     * 토큰의 유효성 및 만료 여부 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // 만료되었거나 변조된 토큰
        }
    }
}
