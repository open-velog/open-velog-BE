package com.openvelog.openvelogbe.common.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRange {
    TO19("1~19"),
    TO29("20~29"),
    TO39("30~39"),
    TO59("40~59"),
    OVER60("60~");

    private final String value;
}
