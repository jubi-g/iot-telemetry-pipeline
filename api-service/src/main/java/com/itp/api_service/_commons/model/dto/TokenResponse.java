package com.itp.api_service._commons.model.dto;

import java.util.Set;

public record TokenResponse(
    String token,
    String type,
    Integer expiry,
    Set<String> scopes
) {
    public static TokenResponse of(String jwt, int ttlSeconds, Set<String> scopes) {
        return new TokenResponse(jwt, "Bearer", ttlSeconds, scopes);
    }
}
