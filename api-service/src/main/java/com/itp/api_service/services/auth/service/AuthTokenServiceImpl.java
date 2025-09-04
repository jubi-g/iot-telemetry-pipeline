package com.itp.api_service.services.auth.service;

import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;
import com.itp.api_service.api.v1.exception.AuthServiceException;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service.services.auth.config.AuthConfig;
import com.itp.api_service.services.auth.jwt.JwtSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthConfig config;
    private final JwtSigner jwtSigner;

    @Override
    public TokenResponse getToken(TokenRequest request) {
        try {
            Set<String> scopes = request.getAllScopes();
            String subject = "service:itp-api";
            String scopeClaim = String.join(" ", scopes);
            Integer ttlSeconds = config.getTokenTtlSeconds();
            String jwt = jwtSigner.sign(
                subject,
                "api",
                ttlSeconds,
                Map.of("scope", scopeClaim));
            return TokenResponse.of(jwt, ttlSeconds, scopes);
        } catch (Exception e) {
            throw new AuthServiceException(ErrorType.SERVICE_ERROR, e.getMessage());
        }
    }
}
