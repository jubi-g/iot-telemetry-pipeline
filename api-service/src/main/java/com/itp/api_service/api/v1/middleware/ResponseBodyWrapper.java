package com.itp.api_service.api.v1.middleware;

import com.itp.api_service.api.v1.model.dto.ApiResponseBody;
import com.itp.api_service._commons.helpers.MdcParam;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseBodyWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return ApiResponseBody.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof ApiResponseBody<?> api)) return body;
        api.getMetadata().setRequestId(MdcParam.requestId());
        HttpStatus status = api.getMetadata().getStatus() != null ? api.getMetadata().getStatus() : HttpStatus.OK;
        response.setStatusCode(status);
        return api;
    }
}
