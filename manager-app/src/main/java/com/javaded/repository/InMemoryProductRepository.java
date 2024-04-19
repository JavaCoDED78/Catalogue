package com.javaded.repository;

import com.javaded.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new CopyOnWriteArrayList<>();

    @Override
    public List<Product> findAllProducts() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public Product save(Product product) {
        product.setId(products.stream()
                .max(Comparator.comparing(Product::getId))
                .map(Product::getId)
                .orElse(0) + 1);
        products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return products.stream()
                .filter(product -> Objects.equals(id, product.getId()))
                .findFirst();
    }

    @Override
    public void deleteById(Integer id) {
        products.removeIf(product -> Objects.equals(id, product.getId()));
    }
}
