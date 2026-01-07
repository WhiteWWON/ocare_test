package com.ocare.Ocare.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RecordType {
    STEPS("S", "steps"),
    ETC("E", "etc");

    private final String code;       // DB 저장용 (S, E)
    private final String jsonValue;  // JSON 입력값 (steps, etc)

    // 변환 로직을 Enum 내부로 캡슐화
    public static RecordType fromJsonType(String jsonType) {
        if (jsonType == null) return ETC;
        String trimmedType = jsonType.trim().toLowerCase();
        return Arrays.stream(RecordType.values())
                .filter(t -> t.jsonValue.equals(trimmedType))
                .findFirst()
                .orElse(ETC);
    }
}
