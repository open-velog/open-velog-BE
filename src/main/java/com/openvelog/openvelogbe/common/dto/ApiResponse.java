package com.openvelog.openvelogbe.common.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ApiResponse<T> {
    enum MessageType {
        EXCEPTION("fail"),
        SUCCESS("success");

        private String message;

        MessageType(String message) {
            this.message = message;
        }

        @JsonValue
        public String getMessage() {
            return message;
        }
    }

    @Schema(type = "integer", example = "200", description = "HTTP status code")
    private int code;

    @Schema(type = "string", example = "success", description = "\"success\" 또는 \"fail\" 로만 옴")
    private MessageType message;

    private T data;

    public static<T> ApiResponse<T> successOf(HttpStatus httpStatus, T dto) {
        return ApiResponse.<T> builder()
                .code(httpStatus.value())
                .message(MessageType.SUCCESS)
                .data(dto)
                .build();
    }

    public static ApiResponse<ErrorResponseDto> failOf(HttpStatus httpStatus, ErrorResponseDto errorResponseDto) {
        return ApiResponse.<ErrorResponseDto> builder()
                .code(httpStatus.value())
                .message(MessageType.SUCCESS)
                .data(errorResponseDto)
                .build();
    }
}