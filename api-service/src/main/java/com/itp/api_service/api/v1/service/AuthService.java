package com.itp.api_service.api.v1.service;

import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;

public interface AuthService {
    TokenResponse getToken(TokenRequest request);
}
