package com.javaded.service;

import com.javaded.entity.Product;

import java.util.Optional;

public interface ProductService {

    Iterable<Product>getAllProducts(String filter);

    Product createProduct(String title, String details);

    Optional<Product> getProduct(Integer id);

    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
