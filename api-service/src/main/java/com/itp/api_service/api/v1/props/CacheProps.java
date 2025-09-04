package com.itp.api_service.api.v1.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.cache")
public class CacheProps {
    private String provider;
    private Integer defaultTtl=60;
    private Integer defaultSize=10000;
}
