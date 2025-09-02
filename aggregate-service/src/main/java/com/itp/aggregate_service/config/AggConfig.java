package com.itp.aggregate_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "agg")
public class AggConfig {
    private String window;
    private int delaySeconds;
    private boolean catchupOnStart;
}
