package com.ocare.Ocare.adapter.in.web.auth;

import com.ocare.Ocare.application.port.in.SignUpCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    /**
     * 어댑터 계층의 데이터(Request)를 애플리케이션 계층의 데이터(Command)로 변환합니다.
     * 이 과정을 통해 컨트롤러가 서비스의 세부 구현에 의존하지 않게 합니다.
     */
    public SignUpCommand toCommand() {
        return SignUpCommand.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .nickname(this.nickname)
                .build();
    }
}
