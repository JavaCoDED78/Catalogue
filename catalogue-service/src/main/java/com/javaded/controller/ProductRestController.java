package com.javaded.controller;

import com.javaded.controller.payload.UpdateProductPayload;
import com.javaded.entity.Product;
import com.javaded.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products/{id:\\d+}")
public class ProductRestController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute
    public Product getProduct(@PathVariable("id") Integer id) {
        return productService.getProduct(id)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping
    public Product receiveProduct(@ModelAttribute Product product) {
        return product;
    }

    @PatchMapping
    public ResponseEntity<?> updateProduct(@PathVariable("id") Integer id,
                                           @Validated @RequestBody UpdateProductPayload payload,
                                           BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw (bindingResult instanceof BindException) ? (BindException) bindingResult : new BindException(bindingResult);
        }
        productService.updateProduct(id, payload.title(), payload.details());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException ex,
                                                                      Locale locale) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                                messageSource.getMessage(ex.getMessage(), new Object[0],
                                        ex.getMessage(), locale))
                );

    }
}
