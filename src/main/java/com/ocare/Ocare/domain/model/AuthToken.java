package com.ocare.Ocare.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthToken {
    private final String accessToken;
    private final String refreshToken;
}
