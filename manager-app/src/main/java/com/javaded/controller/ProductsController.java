package com.javaded.controller;

import com.javaded.client.BadRequestException;
import com.javaded.client.ProductsRestClient;
import com.javaded.controller.payload.NewProductPayload;
import com.javaded.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping("/list")
    public String receiveProductList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", productsRestClient.obtainAllProducts(filter));
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("/create")
    public String receiveNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("/create")
    public String createProduct(NewProductPayload payload, Model model) {
        try {
            Product product = productsRestClient.createProduct(payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/products/new_product";
        }
    }
}
