package com.itp.api_service.api.v1.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI itpApi() {
        return new OpenAPI()
            .info(new Info()
                .title("IoT Telemetry API")
                .version("v1")
                .description("Auth + telemetry stats"));
    }

    @Bean
    Components components() {
        return new Components().addSecuritySchemes("bearer-jwt",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));
    }

    @Bean
    GroupedOpenApi v1Group() {
        return GroupedOpenApi.builder()
            .group("v1")
            .pathsToMatch("/v1/**")
            .build();
    }

}
