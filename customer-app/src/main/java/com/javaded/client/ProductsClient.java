package com.javaded.client;

import com.javaded.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsClient {

    Flux<Product> obtainAllProducts(String filter); // <1>

    Mono<Product> obtainProduct(Integer id);
}
