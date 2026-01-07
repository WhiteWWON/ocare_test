package com.ocare.Ocare.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {
    private final Long memberId;
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
    private final Integer loginFailCnt;
    private final String createdId;
}
