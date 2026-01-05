package com.ocare.Ocare.adapter.in.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 필터가 저장해둔 인증 객체를 꺼냄
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없거나, 익명 사용자(anonymousUser)인 경우 차단
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"로그인이 필요한 서비스입니다.\"}");
            return false; // 더 이상 진행하지 않고 여기서 요청 중단
        }
        // 로그인된 상태라면 컨트롤러(비즈니스 로직)로 통과
        return true;
    }
}
