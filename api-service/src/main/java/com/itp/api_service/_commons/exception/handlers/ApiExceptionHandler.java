package com.itp.api_service._commons.exception.handlers;

import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.exception.DatabaseValidationException;
import com.itp.api_service._commons.helpers.MdcParam;
import com.itp.api_service.api.v1.exception.QueryServiceException;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service.api.v1.model.dto.ApiResponseBody;
import com.itp.api_service.api.v1.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> handleBody(MethodArgumentNotValidException e, HttpServletRequest req) {
        log.error("Validation failed >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.API_VALIDATION_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> handleBind(BindException e, HttpServletRequest req) {
        log.error("Validation failed, bind exception >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.API_VALIDATION_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> handleConstraint(ConstraintViolationException e, HttpServletRequest req) {
        log.error("Validation failed, constraint violations >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.API_VALIDATION_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        log.error("Validation failed, type mismatch >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.API_VALIDATION_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> forbiddenError(AccessDeniedException e, HttpServletRequest req) {
        log.error("Forbidden >> path={} requestId={}",  req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.FORBIDDEN_ERROR);
    }

    @ExceptionHandler(DatabaseUnavailableException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> dbUnavailable(Exception e, HttpServletRequest req) {
        log.error("Database unavailable >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.DATABASE_UNAVAILABLE);
    }

    @ExceptionHandler(DatabaseValidationException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> dbValidationError(Exception e, HttpServletRequest req) {
        log.error("Database validation error >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.DB_VALIDATE_ERROR);
    }

    @ExceptionHandler(QueryServiceException.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> serviceError(Exception e, HttpServletRequest req) {
        log.error("Query service error >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.SERVICE_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseBody<ErrorResponse>> genericError(Exception e, HttpServletRequest req) {
        log.error("Generic error >> path={} requestId={}", req.getRequestURI(), MdcParam.requestId(), e);
        return ErrorType.toErrorResponse(ErrorType.GENERIC_ERROR);
    }

}
