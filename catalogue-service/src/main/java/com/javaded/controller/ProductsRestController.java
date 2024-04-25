package com.javaded.controller;

import com.javaded.controller.payload.NewProductPayload;
import com.javaded.entity.Product;
import com.javaded.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {

    private final ProductService productService;

    @GetMapping
    public Iterable<Product> receiveProducts(@RequestParam(name = "filter", required = false) String filter) {
        return productService.getAllProducts(filter);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Validated @RequestBody NewProductPayload payload,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw (bindingResult instanceof BindException) ? (BindException) bindingResult : new BindException(bindingResult);
        }
        Product product = productService.createProduct(payload.title(), payload.details());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("catalogue-api/products/{id}")
                        .build(Map.of("id", product.getId())))
                .body(product);
    }
}
