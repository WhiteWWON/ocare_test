package com.ocare.Ocare.application.port.in;

import com.ocare.Ocare.domain.model.AuthToken;

public interface LoginUseCase {
    AuthToken login(LoginCommand command);
}
