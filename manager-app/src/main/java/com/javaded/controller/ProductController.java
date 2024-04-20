package com.javaded.controller;

import com.javaded.client.BadRequestException;
import com.javaded.client.ProductsRestClient;
import com.javaded.controller.payload.UpdateProductPayload;
import com.javaded.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products/{id:\\d+}")
public class ProductController {

    private final ProductsRestClient productsRestClient;
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("id") Integer id) {
        return productsRestClient.obtainProduct(id).orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping
    public String receiveProduct() {
        return "catalogue/products/product";
    }

    @GetMapping("/edit")
    public String receiveEditProductPage() {
        return "catalogue/products/edit";

    }

    @PostMapping("/edit")
    public String updateProduct(@ModelAttribute(name = "product", binding = false) Product product,
                                UpdateProductPayload payload, Model model) {
        try {
            productsRestClient.updateProduct(product.id(), payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/products/edit";
        }
    }

    @PostMapping("/delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        productsRestClient.deleteProduct(product.id());
        return "redirect:/catalogue/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e, Model model, HttpServletResponse response,
                                               Locale locale) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("error",
                messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        return "errors/404";
    }
}
