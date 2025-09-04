package com.itp.api_service.api.v1.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service._commons.exception.ServiceException;
import lombok.Getter;

@Getter
public class AuthServiceException extends ServiceException {
    public AuthServiceException(ErrorType error, String message) {
        super(error, message);
    }
}
