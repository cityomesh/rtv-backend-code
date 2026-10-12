package com.rtvnewsnetwork.config.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final String serverUrl;

    public OpenApiConfig(@Value("${api.server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(serverUrl);
        return new OpenAPI().servers(List.of(server));
    }
}