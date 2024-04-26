package com.javaded.service;

import com.javaded.entity.FavouriteProduct;
import com.javaded.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavouriteProductsService implements FavouriteProductsService {

    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId) {
        return favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return favouriteProductRepository.deleteByProductId(productId);
    }

    @Override
    public Mono<FavouriteProduct> getFavouriteProductByProduct(int productId) {
        return favouriteProductRepository.findByProductId(productId);
    }

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts() {
        return favouriteProductRepository.findAll();
    }
}
