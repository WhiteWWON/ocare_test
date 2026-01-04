package com.ocare.Ocare.adapter.in.web.auth;

import com.ocare.Ocare.application.port.in.LoginUseCase;
import com.ocare.Ocare.domain.model.AuthToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthToken token = loginUseCase.login(request.toCommand());
        return ResponseEntity.ok(AuthTokenResponse.from(token));
    }
}
