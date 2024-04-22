package com.javaded.config;

import com.javaded.client.RestClienProductsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClienProductsRestClient productsRestClient(
            @Value("${catalogue.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            @Value("${catalogue.services.catalogue.username:}") String username,
            @Value("${catalogue.services.catalogue.password:}") String password) {
        return new RestClienProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new BasicAuthenticationInterceptor(username, password))
                .build());
    }
}
