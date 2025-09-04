package com.itp.api_service.services.auth.jwt;

import com.itp.api_service.services.auth.config.AuthConfig;
import com.itp.api_service.services.auth.jwt.sign.JwtSignerHs256;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtSignerHs256Test {

    @Test
    void sign_producesValidJwt_withIssuerAudienceAndExpiry() throws ParseException {
        AuthConfig cfg = new AuthConfig();
        byte[] secret = new byte[32];
        for (int i = 0; i < secret.length; i++) secret[i] = (byte) (i + 1);
        cfg.setSignSecret(Base64.getEncoder().encodeToString(secret));
        cfg.setIssuer("https://issuer.example");
        cfg.setTokenTtlSeconds(60);

        JwtSignerHs256 signer = new JwtSignerHs256(cfg);

        String jwt = signer.sign("subj", "api", cfg.getTokenTtlSeconds(), Map.of("scope", "read:stats"));

        SignedJWT parsed = SignedJWT.parse(jwt);
        assertEquals("https://issuer.example", parsed.getJWTClaimsSet().getIssuer());
        assertEquals("subj", parsed.getJWTClaimsSet().getSubject());
        assertEquals("api", parsed.getJWTClaimsSet().getAudience().getFirst());

        Date exp = parsed.getJWTClaimsSet().getExpirationTime();
        assertNotNull(exp);
        long secondsToExp = (exp.toInstant().getEpochSecond() - Instant.now().getEpochSecond());
        assertTrue(secondsToExp <= 60 && secondsToExp > 0, "expiry should be ~60s in the future");
    }
}
