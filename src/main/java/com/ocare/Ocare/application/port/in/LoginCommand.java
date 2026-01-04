package com.ocare.Ocare.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {
    private final String email;
    private final String password;
}