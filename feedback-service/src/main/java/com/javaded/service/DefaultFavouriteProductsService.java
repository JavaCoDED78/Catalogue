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
    public Mono<FavouriteProduct> addProductToFavourites(int productId, String userId) {
        return favouriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return favouriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavouriteProduct> getFavouriteProductByProduct(int productId, String userId) {
        return favouriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts(String userId) {
        return favouriteProductRepository.findAllByUserId(userId);
    }
}
