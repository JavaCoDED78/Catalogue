package com.javaded.service;


import com.javaded.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsService {
    
    Mono<FavouriteProduct> addProductToFavourites(int productId);
    
    Mono<Void> removeProductFromFavourites(int productId);

    Mono<FavouriteProduct> getFavouriteProductByProduct(int productId);

    Flux<FavouriteProduct> getFavouriteProducts();
}
