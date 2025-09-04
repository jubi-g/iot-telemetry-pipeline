package com.itp.api_service.services.auth.jwt;

import com.itp.api_service._commons.exception.JwtSignException;

import java.util.Map;

public interface JwtSigner {
    /**
     * Create an access token with standard claims:
     *  - iss, aud, sub, iat, exp, jti
     *  - plus distinct set of scopes
     * @return JWT string
     */
    String sign(String subject, String audience, int ttlSeconds, Map<String, Object> customClaims) throws JwtSignException;
}
