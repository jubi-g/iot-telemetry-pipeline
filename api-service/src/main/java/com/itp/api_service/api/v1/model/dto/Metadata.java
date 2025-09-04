package com.itp.api_service.api.v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private UUID requestId;
    private HttpStatus status;

    public Metadata(HttpStatus status) {
        this.status = status;
    }
}
