package com.javaded.service;


import com.javaded.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsService {
    
    Mono<FavouriteProduct> addProductToFavourites(int productId, String userId);
    
    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<FavouriteProduct> getFavouriteProductByProduct(int productId, String userId);

    Flux<FavouriteProduct> getFavouriteProducts(String userId);
}
