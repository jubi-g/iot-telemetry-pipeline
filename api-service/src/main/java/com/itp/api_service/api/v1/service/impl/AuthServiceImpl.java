package com.itp.api_service.api.v1.service.impl;

import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;
import com.itp.api_service.api.v1.service.AuthService;
import com.itp.api_service.services.auth.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthTokenService service;

    @Override
    public TokenResponse getToken(TokenRequest request) {
        return service.getToken(request);
    }

}
