package com.itp.api_service._commons.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import lombok.Getter;

@Getter
public class DatabaseUnavailableException extends RuntimeException {
    private final ErrorType error;

    public DatabaseUnavailableException(ErrorType error, Exception e) {
        super(e);
        this.error = error;
    }
}
