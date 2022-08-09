package com.testproject.WbPriceTrackerApi.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApi30Config {

    @Value("${api.title}")
    private String API_TITLE;
    @Value("${api.version}")
    private String API_VERSION;
    @Value("${api.description}")
    private String API_DESCRIPTION;
    @Value("${server.port}")
    private String SERVER_PORT;

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title(API_TITLE)
                        .version(API_VERSION)
                        .description(API_DESCRIPTION)
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(new Server().url("http://localhost:"+SERVER_PORT)
                        .description("Dev service")));
    }

    @Bean
    public GroupedOpenApi publicUserApi() {
        return GroupedOpenApi.builder()
                .group(API_TITLE)
//                .pathsToMatch("/auth/**", "/api/v1/users/**", "/api/v1/admin/**")
                .pathsToExclude("/api/v1/parser")
                .build();
    }
}
