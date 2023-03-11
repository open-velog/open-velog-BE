package com.openvelog.openvelogbe.common.entity.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    M("man"),
    F("woman");

    @JsonValue
    private final String value;
}