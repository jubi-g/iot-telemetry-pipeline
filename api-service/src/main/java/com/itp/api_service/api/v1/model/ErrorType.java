package com.itp.api_service.api.v1.model;

import com.itp.api_service.api.v1.model.dto.ApiResponseBody;
import com.itp.api_service.api.v1.model.dto.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ErrorType {
    GENERIC_ERROR           ("1000", "generic_error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_ERROR           ("1001", "service_error", HttpStatus.UNPROCESSABLE_ENTITY),
    SERVICE_UNAVAILABLE     ("1002", "service_unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_UNAVAILABLE    ("2001", "internal_service_error", HttpStatus.SERVICE_UNAVAILABLE),
    DB_VALIDATE_ERROR       ("2002", "internal_service_error", HttpStatus.CONFLICT),
    JWT_SIGN_ERROR          ("3001", "jwt_sign_error", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorType(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.httpStatus = status;
    }

    public static Optional<ErrorType> fromCode(String code) {
        if (code == null) return Optional.empty();
        return Arrays.stream(values())
            .filter(c -> c.code.equalsIgnoreCase(code.trim()))
            .findFirst();
    }

    public static ResponseEntity<ApiResponseBody<ErrorResponse>> toErrorResponse(ErrorType type) {
        HttpStatus status = type.getHttpStatus();
        ApiResponseBody<ErrorResponse> response = new ApiResponseBody<>(status, new ErrorResponse(type.getCode(), type.getMessage()));
        return ResponseEntity.status(status).body(response);
    }
}
