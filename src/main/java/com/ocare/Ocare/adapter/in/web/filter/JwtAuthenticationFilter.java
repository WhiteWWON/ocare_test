package com.ocare.Ocare.adapter.in.web.filter;

import com.ocare.Ocare.global.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter : "요청당 한 번만" 실행됨을 보장합니다. 불필요하게 인증 로직이 여러 번 도는 것을 방지
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // "Bearer " 뒷부분인 실제 토큰 값만 추출
            // 2. 토큰 검증
            if (jwtTokenProvider.validateToken(token)) { // JwtTokenProvider를 사용하여 토큰이 유효한지 검증 (서명, 만료시간 등)
                String email = jwtTokenProvider.getEmail(token); // 유효하다면 토큰에서 사용자의 이메일을 꺼냄
                // 3. 스프링 시큐리티 전용 인증 객체(Authentication) 생성 및 SecurityContext 등록
                // 파라미터: 1.Principal(사용자ID), 2.Credentials(비번-이미 검증됐으니 null), 3.Authorities(권한)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                // 생성한 인증 객체를 SecurityContextHolder(메모리)에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }
}
