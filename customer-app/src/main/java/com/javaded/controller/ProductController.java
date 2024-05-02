package com.javaded.controller;

import com.javaded.client.FavouriteProductsClient;
import com.javaded.client.ProductReviewsClient;
import com.javaded.client.ProductsClient;
import com.javaded.client.exception.ClientBadRequestException;
import com.javaded.controller.payload.NewProductReviewPayload;
import com.javaded.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
@RequestMapping("/customer/products/{productId:\\d+}")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductsClient productsClient;
    private final FavouriteProductsClient favouriteProductsClient;
    private final ProductReviewsClient productReviewsClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") int id) {
        return productsClient.obtainProduct(id)
                .switchIfEmpty(Mono.defer(
                        () -> Mono.error(new NoSuchElementException("customer.products.error.not_found"))
                ));
    }

    @GetMapping
    public Mono<String> receiveProductPage(@ModelAttribute("product") Mono<Product> productMono,
                                           Model model) {
        model.addAttribute("inFavourite", false);
        return productMono
                .flatMap(product -> productReviewsClient.obtainProductReviewsByProduct(product.id())
                        .collectList()
                        .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                        .then(favouriteProductsClient.obtainFavouriteProductByProductId(product.id())
                                .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true)))
                        .thenReturn("customer/products/product"));
    }

    @PostMapping("/add-to-favourites")
    public Mono<String> addProductToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductsClient.addProductToFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId))
                        .onErrorResume(exception -> {
                            log.error(exception.getMessage(), exception);
                            return Mono.just("redirect:/customer/products/%d".formatted(productId));
                        }));
    }

    @PostMapping("remove-from-favourites")
    public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favouriteProductsClient.removeProductFromFavourites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(@ModelAttribute("product") Mono<Product> productMono,
                                     NewProductReviewPayload payload,
                                     Model model,
                                     ServerHttpResponse response) {
        return productMono
                .flatMap(product -> productReviewsClient
                        .createProductReview(product.id(), payload.rating(), payload.review())
                        .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                        .onErrorResume(ClientBadRequestException.class, exception -> {
                            model.addAttribute("inFavourite", false);
                            model.addAttribute("payload", payload);
                            model.addAttribute("errors", exception.getErrors());
                            response.setStatusCode(HttpStatus.BAD_REQUEST);
                            return favouriteProductsClient.obtainFavouriteProductByProductId(product.id())
                                    .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                                    .thenReturn("customer/products/product");
                        }));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               ServerHttpResponse response) {
        model.addAttribute("error", exception.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND);
        return "errors/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        return Objects.requireNonNull(exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName()))
                .doOnSuccess(token -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
