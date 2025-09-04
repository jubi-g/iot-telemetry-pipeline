package com.itp.api_service.services.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    private String signSecret;
    private String issuer;
    private Integer tokenTtlSeconds = 900;
}
