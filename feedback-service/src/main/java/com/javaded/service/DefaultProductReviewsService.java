package com.javaded.service;

import com.javaded.entity.ProductReview;
import com.javaded.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductReviewsService implements ProductReviewsService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId) {
        return productReviewRepository.save(
                new ProductReview(UUID.randomUUID(), productId, rating, review, userId));
    }

    @Override
    public Flux<ProductReview> getProductReviewsByProduct(int productId) {
        return productReviewRepository.findAllByProductId(productId);
    }
}
