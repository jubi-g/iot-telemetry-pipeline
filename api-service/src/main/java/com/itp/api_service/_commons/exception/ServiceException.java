package com.itp.api_service._commons.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final ErrorType error;

    public ServiceException(ErrorType error, String message) {
        super(message);
        this.error = error;
    }
}
