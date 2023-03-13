package com.openvelog.openvelogbe.common;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.dto.ErrorResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorType;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleException(Exception e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseData);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleRuntimeException(RuntimeException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.RUNTIME_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseData);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.ENTITY_NOT_FOUND_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.BAD_REQUEST, errorResponseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseData);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<ErrorResponseDto>> handleValidationException(ValidationException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.VALIDATION_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseData);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.JWT_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseData);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.ILLEGAL_ARGUMENT_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseData);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.ACCESS_DENIED_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.INTERNAL_SERVER_ERROR, errorResponseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseData);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleAccessDeniedException(BadCredentialsException e) {
        log.error(e.toString() + " occurred: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorType.BAD_CREDENTIALS_EXCEPTION, e.getMessage());
        ApiResponse<ErrorResponseDto> errorResponseData = ApiResponse.failOf(HttpStatus.UNAUTHORIZED, errorResponseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseData);
    }
}
