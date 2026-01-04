package com.ocare.Ocare.application.port.in;

import lombok.Builder;
import lombok.Getter;

// 입력 데이터 검증용
@Getter
@Builder
public class SignUpCommand {
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
}
