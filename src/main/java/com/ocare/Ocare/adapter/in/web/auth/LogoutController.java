package com.ocare.Ocare.adapter.in.web.auth;

import com.ocare.Ocare.application.port.in.LogoutUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutController {
    private final LogoutUseCase logoutUseCase;
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 1. 인터셉터를 통과했으므로 SecurityContext에 인증 정보가 있음
        String bearerToken = request.getHeader("Authorization");
        String accessToken = bearerToken.substring(7);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. 로그아웃 처리
        logoutUseCase.logout(email, accessToken);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
