package com.itp.api_service.api.v1.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseBody<T> {
    private T data = null;
    private ErrorResponse error = null;
    private Metadata metadata = new Metadata();

    public ApiResponseBody(HttpStatus status, T data) {
        this.data = data;
        this.metadata = new Metadata(status);
    }

    public ApiResponseBody(HttpStatus status, ErrorResponse error) {
        this.error = error;
        this.metadata = new Metadata(status);
    }
}
