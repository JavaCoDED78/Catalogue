package com.javaded.controller;

import com.javaded.controller.payload.UpdateProductPayload;
import com.javaded.entity.Product;
import com.javaded.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ProductRestControllerTest {

    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductRestController controller;

    @Test
    void receiveProduct_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1, "Название товара", "Описание товара");
        doReturn(Optional.of(product)).when(productService).getProduct(1);

        // when
        var result = controller.getProduct(1);

        // then
        assertEquals(product, result);
    }

    @Test
    void receiveProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given

        // when
        var exception = assertThrows(NoSuchElementException.class, () -> controller.getProduct(1));

        // then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
    }

    @Test
    void findProduct_ReturnsProduct() {
        // given
        var product = new Product(1, "Название товара", "Описание товара");

        // when
        var result = controller.receiveProduct(product);

        // then
        assertEquals(product, result);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new UpdateProductPayload("Новое название", "Новое описание");
        var bindingResult = new MapBindingResult(Map.of(), "payload");

        // when
        var result = controller.updateProduct(1, payload, bindingResult);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(productService).updateProduct(1, "Новое название", "Новое описание");
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new UpdateProductPayload("   ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> controller.updateProduct(1, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void updateProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new UpdateProductPayload("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> controller.updateProduct(1, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void deleteProduct_ReturnsNoContent() {
        // given

        // when
        var result = controller.deleteProduct(1);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(productService).deleteProduct(1);
    }

    @Test
    void handleNoSuchElementException_ReturnsNotFound() {
        // given
        var exception = new NoSuchElementException("error_code");
        var locale = Locale.of("ru");

        doReturn("error details").when(messageSource)
                .getMessage("error_code", new Object[0], "error_code", Locale.of("ru"));

        // when
        var result = controller.handleNoSuchElementException(exception, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());

        verifyNoInteractions(productService);
    }
}
