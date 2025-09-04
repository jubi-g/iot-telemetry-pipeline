package com.itp.api_service.services.auth.jwt.sign;

import com.itp.api_service._commons.exception.JwtSignException;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service.services.auth.config.AuthConfig;
import com.itp.api_service.services.auth.jwt.JwtSigner;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtSignerHs256 implements JwtSigner {

    private final AuthConfig config;

    @Override
    public String sign(String subject, String audience, int ttlSeconds, Map<String, Object> customClaims) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet.Builder b = new JWTClaimsSet.Builder()
                .issuer(config.getIssuer())
                .audience(audience)
                .subject(subject)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(ttlSeconds)))
                .jwtID(UUID.randomUUID().toString());
            if (customClaims != null) {
                for (Map.Entry<String, Object> e : customClaims.entrySet()) {
                    b.claim(e.getKey(), e.getValue());
                }
            }
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), b.build());
            jwt.sign(new MACSigner(Base64.getDecoder().decode(config.getSignSecret())));
            return jwt.serialize();
        } catch (Exception e) {
            throw new JwtSignException(ErrorType.JWT_SIGN_ERROR, e);
        }
    }

}