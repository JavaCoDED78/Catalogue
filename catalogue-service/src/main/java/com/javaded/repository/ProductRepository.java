package com.javaded.repository;

import com.javaded.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAllProducts();

    Product save(Product product);

    Optional<Product> findById(Integer id);

    void deleteById(Integer id);
}
