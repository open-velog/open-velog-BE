package com.openvelog.openvelogbe.common.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    MEMBER_NOT_FOUND("해당 사용자가 존재하지 않습니다."),
    BOARD_NOT_FOUND("해당 댓글이 존재하지 않습니다."),

    AUTHENTICATION_FAILED("JWT가 올바르지 않습니다"),
    ACCESS_DENIED("권한이 없습니다."),

    USERNAME_DUPLICATION("username이 중복됐습니다."),
    USERID_DUPLICATION("userid가 중복됐습니다."),
    WRONG_USERNAME("username이 일치하지 않습니다."),
    WRONG_PASSWORD("패스워드가 틀렸습니다."),
    WRONG_JWT_TOKEN("JWT Token이 잘못되었습니다."),
    NO_BLOG("블로그가 없습니다.");

    private final String message;

    @JsonValue
    public String getMessage() {
        return this.message;
    }
}