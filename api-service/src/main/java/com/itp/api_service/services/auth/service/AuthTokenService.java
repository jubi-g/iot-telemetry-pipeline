package com.itp.api_service.services.auth.service;

import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;

public interface AuthTokenService {
    TokenResponse getToken(TokenRequest request);
}
