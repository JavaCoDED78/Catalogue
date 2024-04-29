package com.javaded.repository;

import com.javaded.entity.ProductReview;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryProductReviewRepository implements ProductReviewRepository {

    private final List<ProductReview> productReviews = new CopyOnWriteArrayList<>(Collections.emptyList());

    @Override
    public Mono<ProductReview> save(ProductReview productReview) {
        productReviews.add(productReview);
        return Mono.just(productReview);
    }

    @Override
    public Flux<ProductReview> findAllByProductId(int productId) {
        return Flux.fromIterable(productReviews)
                .filter(productReview -> productReview.getProductId() == productId);
    }
}
