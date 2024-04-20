package com.javaded.client;

import com.javaded.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClient {

    List<Product> obtainAllProducts();

    Product createProduct(String title, String details);

    Optional<Product> obtainProduct(Integer id);

    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
