package com.ocare.Ocare.adapter.in.web.auth;

import com.ocare.Ocare.application.port.in.SignUpUseCase;
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
public class MemberSignupController {
    private final SignUpUseCase signUpUseCase;

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) { //유효성 체크를 위해 @Valid 어노테이션 추가
        // request DTO를 Command로 변환하여 전달
        signUpUseCase.signUp(request.toCommand());
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
