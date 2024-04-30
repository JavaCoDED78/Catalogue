package com.javaded.service;

import com.javaded.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId);

    Flux<ProductReview> getProductReviewsByProduct(int productId);
}
