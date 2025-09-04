package com.itp.api_service._commons.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import lombok.Getter;

@Getter
public class JwtSignException extends RuntimeException {
    private final ErrorType error;

    public JwtSignException(ErrorType error, Exception e) {
        super(e);
        this.error = error;
    }
}
