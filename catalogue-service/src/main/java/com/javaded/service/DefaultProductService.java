package com.javaded.service;

import com.javaded.entity.Product;
import com.javaded.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Iterable<Product> getAllProducts(String filter) {
        if (filter != null && !filter.isBlank()) {
            return productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        }
        return productRepository.findAll();
    }


    @Override
    @Transactional
    public Product createProduct(String title, String details) {
        return productRepository.save(new Product(null, title, details));
    }

    @Override
    public Optional<Product> getProduct(Integer id) {
        return productRepository.findById(id);
    }


    @Override
    @Transactional
    public void updateProduct(Integer id, String title, String details) {
        productRepository.findById(id).ifPresentOrElse(product -> {
                    product.setTitle(title);
                    product.setDetails(details);
                }, () -> {
                    throw new NoSuchElementException();
                }
        );
    }


    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }
}
