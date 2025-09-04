package com.itp.api_service.api.v1.exception;

import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service._commons.exception.ServiceException;
import lombok.Getter;

@Getter
public class QueryServiceException extends ServiceException {
    public QueryServiceException(ErrorType error, String message) {
        super(error, message);
    }
}
