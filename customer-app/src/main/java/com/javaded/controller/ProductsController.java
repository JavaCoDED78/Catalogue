package com.javaded.controller;

import com.javaded.client.ProductsClient;
import com.javaded.entity.FavouriteProduct;
import com.javaded.service.FavouriteProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsClient productsClient;

    private final FavouriteProductsService favouriteProductsService;

    @GetMapping("/list")
    public Mono<String> receiveProductListPage(Model model,
                                               @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return productsClient.obtainAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("/favourites")
    public Mono<String> receiveFavouriteProductsPage(Model model,
                                                 @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return favouriteProductsService.getFavouriteProducts()
                .map(FavouriteProduct::getProductId)
                .collectList()
                .flatMap(favouriteProducts -> productsClient.obtainAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favourites");
    }
}
