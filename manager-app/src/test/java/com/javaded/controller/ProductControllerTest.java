package com.javaded.controller;

import com.javaded.client.BadRequestException;
import com.javaded.client.ProductsRestClient;
import com.javaded.controller.payload.UpdateProductPayload;
import com.javaded.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductController controller;

    @Test
    void product_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        doReturn(Optional.of(product)).when(productsRestClient).obtainProduct(1);

        // when
        var result = controller.product(1);

        // then
        assertEquals(product, result);

        verify(productsRestClient).obtainProduct(1);
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void product_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> controller.product(1));

        // then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());

        verify(productsRestClient).obtainProduct(1);
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void receiveProduct_ReturnsProductPage() {
        // given

        // when
        var result = controller.receiveProduct();

        // then
        assertEquals("catalogue/products/product", result);

        verifyNoInteractions(productsRestClient);
    }

    @Test
    void receiveEditProductPage_ReturnsProductEditPage() {
        // given

        // when
        var result = controller.receiveEditProductPage();

        // then
        assertEquals("catalogue/products/edit", result);

        verifyNoInteractions(productsRestClient);
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductPayload("Новое название", "Новое описание");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        // when
        var result = controller.updateProduct(product, payload, model, response);

        // then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(productsRestClient).updateProduct(1, "Новое название", "Новое описание");
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductPayload("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productsRestClient).updateProduct(1, "   ", null);

        // when
        var result = controller.updateProduct(product, payload, model, response);

        // then
        assertEquals("catalogue/products/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productsRestClient).updateProduct(1, "   ", null);
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void deleteProduct_RedirectsToProductsListPage() {
        // given
        var product = new Product(1, "Товар №1", "Описание товара №1");

        // when
        var result = controller.deleteProduct(product);

        // then
        assertEquals("redirect:/catalogue/products/list", result);

        verify(productsRestClient).deleteProduct(1);
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        // given
        var exception = new NoSuchElementException("error");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var locale = Locale.of("ru");

        doReturn("Ошибка").when(messageSource)
                .getMessage("error", new Object[0], "error", Locale.of("ru"));

        // when
        var result = controller.handleNoSuchElementException(exception, model, response, locale);

        // then
        assertEquals("errors/404",  result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(messageSource).getMessage("error", new Object[0], "error", Locale.of("ru"));
        verifyNoMoreInteractions(messageSource);
        verifyNoInteractions(productsRestClient);
    }
}
