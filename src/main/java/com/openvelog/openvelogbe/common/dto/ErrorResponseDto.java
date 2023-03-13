package com.openvelog.openvelogbe.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponseDto {
    ErrorType errorType;
    String errorMessage;

    public static ErrorResponseDto of(ErrorType errorType, String errorMessage) {
        return ErrorResponseDto.builder()
                .errorType(errorType)
                .errorMessage(errorMessage)
                .build();
    }
}
