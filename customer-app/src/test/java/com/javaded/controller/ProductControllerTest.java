package com.javaded.controller;

import com.javaded.client.FavouriteProductsClient;
import com.javaded.client.ProductReviewsClient;
import com.javaded.client.ProductsClient;
import com.javaded.client.exception.ClientBadRequestException;
import com.javaded.controller.payload.NewProductReviewPayload;
import com.javaded.entity.FavouriteProduct;
import com.javaded.entity.Product;
import com.javaded.entity.ProductReview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductsClient productsClient;
    @Mock
    FavouriteProductsClient favouriteProductsClient;
    @Mock
    ProductReviewsClient productReviewsClient;

    @Test
    void handleNoSuchElementException_ReturnsErrors404() {
        //given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        //when
        var result = productController.handleNoSuchElementException(exception, model, response);

        //then
        assertEquals("errors/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removeProductFromFavourites_RedirectsToProductPage() {
        // given
        doReturn(Mono.empty()).when(favouriteProductsClient).removeProductFromFavourites(1);

        // when
        StepVerifier.create(productController.removeProductFromFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsClient).removeProductFromFavourites(1);
        verifyNoMoreInteractions(favouriteProductsClient);
        verifyNoInteractions(productsClient, productReviewsClient);
    }

    @Test
    void loadProduct_ProductExists_ReturnsNotEmptyMono() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        doReturn(Mono.just(product)).when(productsClient).obtainProduct(1);

        // when
        StepVerifier.create(productController.loadProduct(1))
                // then
                .expectNext(new Product(1, "Товар №1", "Описание товара №1"))
                .verifyComplete();

        verify(productsClient).obtainProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favouriteProductsClient, productReviewsClient);
    }

    @Test
    void loadProduct_ProductDoesNotExist_ReturnsMonoWithNoSuchElementException() {
        // given
        doReturn(Mono.empty()).when(productsClient).obtainProduct(1);

        // when
        StepVerifier.create(productController.loadProduct(1))
                // then
                .expectErrorMatches(exception -> exception instanceof NoSuchElementException e &&
                                                 e.getMessage().equals("customer.products.error.not_found"))
                .verify();

        verify(productsClient).obtainProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favouriteProductsClient, productReviewsClient);
    }

    @Test
    void receiveProductPage_ReturnsProductPage() {
        // given
        var model = new ConcurrentModel();
        var productReviews = List.of(
                new ProductReview(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"),
                        1, 5, "На пятёрочку"),
                new ProductReview(UUID.fromString("849c3fac-cbaa-11ee-af68-737c6d37214a"),
                        1, 4, "Могло быть и лучше"));

        doReturn(Flux.fromIterable(productReviews)).when(productReviewsClient).obtainProductReviewsByProduct(1);

        var favouriteProduct = new FavouriteProduct(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1);
        doReturn(Mono.just(favouriteProduct)).when(favouriteProductsClient).obtainFavouriteProductByProductId(1);

        // when
        StepVerifier.create(productController.receiveProductPage(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")), model))
                // then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(productReviews, model.getAttribute("reviews"));
        assertEquals(true, model.getAttribute("inFavourite"));

        verify(productReviewsClient).obtainProductReviewsByProduct(1);
        verify(favouriteProductsClient).obtainFavouriteProductByProductId(1);
        verifyNoMoreInteractions(productsClient, favouriteProductsClient);
        verifyNoInteractions(productsClient);
    }

    @Test
    void addProductToFavourites_RequestIsValid_RedirectsToProductPage() {
        // given
        doReturn(Mono.just(new FavouriteProduct(UUID.fromString("25ec67b4-cbac-11ee-adc8-4bd80e8171c4"), 1)))
                .when(favouriteProductsClient).addProductToFavourites(1);

        // when
        StepVerifier.create(productController.addProductToFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsClient).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductsClient);
        verifyNoInteractions(productReviewsClient, productsClient);
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_RedirectsToProductPage() {
        // given
        doReturn(Mono.error(new ClientBadRequestException("Возникла какая-то ошибка", null,
                List.of("Какая-то ошибка"))))
                .when(favouriteProductsClient).addProductToFavourites(1);

        // when
        StepVerifier.create(productController.addProductToFavourites(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1"))))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteProductsClient).addProductToFavourites(1);
        verifyNoMoreInteractions(favouriteProductsClient);
        verifyNoInteractions(productReviewsClient, productsClient);
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        doReturn(Mono.just(new ProductReview(UUID.fromString("86efa22c-cbae-11ee-ab01-679baf165fb7"), 1, 3, "Ну, на троечку...")))
                .when(productReviewsClient).createProductReview(1, 3, "Ну, на троечку...");

        // when
        StepVerifier.create(productController.createReview(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")),
                        new NewProductReviewPayload(3, "Ну, на троечку..."), model, response))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        assertNull(response.getStatusCode());

        verify(productReviewsClient).createProductReview(1, 3, "Ну, на троечку...");
        verifyNoMoreInteractions(productReviewsClient);
        verifyNoInteractions(productsClient, favouriteProductsClient);
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPageWithPayloadAndErrors() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        var favouriteProduct = new FavouriteProduct(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1);
        doReturn(Mono.just(favouriteProduct)).when(favouriteProductsClient).obtainFavouriteProductByProductId(1);

        doReturn(Mono.error(new ClientBadRequestException("Возникла какая-то ошибка",
                null, List.of("Ошибка 1", "Ошибка 2"))))
                .when(productReviewsClient).createProductReview(1, null, "Очень длинный отзыв");

        // when
        StepVerifier.create(productController.createReview(
                        Mono.just(new Product(1, "Товар №1", "Описание товара №1")),
                        new NewProductReviewPayload(null, "Очень длинный отзыв"), model, response))
                // then
                .expectNext("customer/products/product")
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(true, model.getAttribute("inFavourite"));
        assertEquals(new NewProductReviewPayload(null, "Очень длинный отзыв"), model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(productReviewsClient).createProductReview(1, null, "Очень длинный отзыв");
        verify(favouriteProductsClient).obtainFavouriteProductByProductId(1);
        verifyNoMoreInteractions(productReviewsClient, favouriteProductsClient);
        verifyNoInteractions(productsClient);
    }
}