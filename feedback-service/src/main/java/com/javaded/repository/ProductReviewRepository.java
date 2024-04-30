package com.javaded.repository;

import com.javaded.entity.ProductReview;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ProductReviewRepository extends ReactiveCrudRepository<ProductReview, UUID>  {

    Flux<ProductReview> findAllByProductId(int productId);
}
