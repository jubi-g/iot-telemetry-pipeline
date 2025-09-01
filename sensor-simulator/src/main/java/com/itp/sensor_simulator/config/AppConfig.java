package com.itp.sensor_simulator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String topic;
    private Integer housesCount;
    private List<String> zones;
}
