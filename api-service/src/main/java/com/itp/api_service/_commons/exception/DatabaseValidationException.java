package com.itp.api_service._commons.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import lombok.Getter;

@Getter
public class DatabaseValidationException extends RuntimeException {
    private final ErrorType error;

    public DatabaseValidationException(ErrorType error, String message) {
        super(message);
        this.error = error;
    }
}
