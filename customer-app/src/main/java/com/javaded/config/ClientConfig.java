package com.javaded.config;

import com.javaded.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${catalogue.services.catalogue.uri:http://localhost:8081}") String baseUrl
    ) {
        return new WebClientProductsClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build());
    }
}
