package com.muradtek.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI orderMatchingEngineAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Info info = new Info()
                .title("Order Matching Engine API")
                .version("1.0.0")
                .description("REST API for a limit order matching engine with price-time priority algorithm. " +
                        "Supports multiple trading symbols with real-time order matching and trade execution.");

        return new OpenAPI()
                .info(info)
                .addServersItem(localServer);
    }
}
