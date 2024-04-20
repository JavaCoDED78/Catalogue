package com.javaded.service;

import com.javaded.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product>getAllProducts();

    Product createProduct(String title, String details);

    Optional<Product> getProduct(Integer id);

    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
