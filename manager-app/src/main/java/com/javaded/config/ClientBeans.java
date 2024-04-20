package com.javaded.config;

import com.javaded.client.RestClienProductsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClienProductsRestClient productsRestClient(
            @Value("${catalogue.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri) {
        return new RestClienProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
