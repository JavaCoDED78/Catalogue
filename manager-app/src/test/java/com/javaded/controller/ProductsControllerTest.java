package com.javaded.controller;

import com.javaded.client.BadRequestException;
import com.javaded.client.ProductsRestClient;
import com.javaded.controller.payload.NewProductPayload;
import com.javaded.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController productsController;

    @Test
    void getProductsList_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "товар";
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i),
                        "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productsRestClient).obtainAllProducts(filter);

        // when
        var result = productsController.receiveProductList(model, filter);

        // then
        assertEquals("catalogue/products/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("products"));
        verify(productsRestClient).obtainAllProducts(filter);
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = productsController.receiveNewProductPage();

        // then
        assertEquals("catalogue/products/new_product", result);
    }

    @Test
    void createProduct_requestIsValid_ReturnsRedirectToProductPage() {
        //given
        var payload = new NewProductPayload("Новый товар", "Описание нового товара");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1, "Новый товар", "Описание нового товара"))
                .when(productsRestClient)
                .createProduct("Новый товар", "Описание нового товара");

        //when
        var result = productsController.createProduct(payload, model, response);

        //then
        assertEquals("redirect:/catalogue/products/%d".formatted(1), result);
        verify(productsRestClient).createProduct("Новый товар", "Описание нового товара");
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void createProduct_requestIsInvalid_ReturnsProductFormWithErrors() {
        //given
        var payload = new NewProductPayload("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Error 1", "Error 2")))
                .when(productsRestClient)
                .createProduct("   ", null);

        //when
        var result = productsController.createProduct(payload, model, response);

        //then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Error 1", "Error 2"), model.getAttribute("errors"));
        verify(productsRestClient).createProduct("   ", null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        verifyNoMoreInteractions(productsRestClient);
    }
}