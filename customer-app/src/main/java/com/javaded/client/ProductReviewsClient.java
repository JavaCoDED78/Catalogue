package com.javaded.client;

import com.javaded.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsClient {

    Flux<ProductReview> obtainProductReviewsByProduct(Integer productId);

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);
}
