package com.javaded.repository;


import com.javaded.entity.FavouriteProduct;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryFavouriteProductRepository implements FavouriteProductRepository {

    private final List<FavouriteProduct> favouriteProducts = new CopyOnWriteArrayList<>(Collections.emptyList());

    @Override
    public Mono<FavouriteProduct> save(FavouriteProduct favouriteProduct) {
        favouriteProducts.add(favouriteProduct);
        return Mono.just(favouriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        favouriteProducts.removeIf(favouriteProduct -> favouriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(favouriteProducts)
                .filter(favouriteProduct ->  favouriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(favouriteProducts);
    }
}
