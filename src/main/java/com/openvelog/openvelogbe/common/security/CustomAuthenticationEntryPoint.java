package com.openvelog.openvelogbe.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.dto.ErrorResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ErrorResponseDto ERROR_RESPONSE_DTO = ErrorResponseDto.of(ErrorType.AUTHENTICATION_EXCEPTION,
            ErrorMessage.AUTHENTICATION_FAILED.getMessage());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ApiResponse.failOf(HttpStatus.UNAUTHORIZED, ERROR_RESPONSE_DTO));
            os.flush();
        }
    }
}