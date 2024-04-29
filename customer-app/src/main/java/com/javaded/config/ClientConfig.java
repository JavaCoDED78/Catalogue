package com.javaded.config;

import com.javaded.client.WebClientFavouriteProductsClient;
import com.javaded.client.WebClientProductReviewsClient;
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

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            @Value("${catalogue.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl
    ) {
        return new WebClientFavouriteProductsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${catalogue.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl
    ) {
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
