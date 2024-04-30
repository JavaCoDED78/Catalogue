package com.javaded.controller;

import com.javaded.controller.payload.NewFavouriteProductPayload;
import com.javaded.entity.FavouriteProduct;
import com.javaded.service.FavouriteProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(("feedback-api/favourite-products"))
@RequiredArgsConstructor
public class FavouriteProductsRestController {

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping
    public Flux<FavouriteProduct> receiveFavouriteProducts(Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono
                .flatMapMany(token -> favouriteProductsService.getFavouriteProducts(token.getToken().getSubject()));
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> receiveFavouriteProductByProductId(Mono<JwtAuthenticationToken> authenticationTokenMono,
                                                                     @PathVariable("productId") int productId) {
        return authenticationTokenMono.flatMap(token -> favouriteProductsService
                .getFavouriteProductByProduct(productId, token.getToken().getSubject()));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavourites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Validated @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return Mono.zip(authenticationTokenMono, payloadMono)
                .flatMap(tuple -> favouriteProductsService
                        .addProductToFavourites(tuple.getT2().productId(), tuple.getT1().getToken().getSubject()))
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/favourite-products/{id}")
                                .build(Map.of("id", favouriteProduct.getId())))
                        .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> deleteProductFromFavourites(Mono<JwtAuthenticationToken> authenticationTokenMono,
                                                                  @PathVariable("productId") int productId) {
        return authenticationTokenMono
                .flatMap(token -> favouriteProductsService.removeProductFromFavourites(productId, token.getToken().getSubject()))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
