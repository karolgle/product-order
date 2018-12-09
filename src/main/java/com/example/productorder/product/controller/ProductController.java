package com.example.productorder.product.controller;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.PriceUpdateInfo;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.domain.ProductInfo;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.helper.ProductHelper;
import com.example.productorder.product.service.PriceService;
import com.example.productorder.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
@RestController
@RequestMapping("/")
@Api(value="Products", description="Operations pertaining to Products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final PriceService priceService;


    @Autowired
    public ProductController(ProductService productService, PriceService priceService) {
        this.productService = productService;
        this.priceService = priceService;
    }

    @GetMapping("/products/{name}")
    @ApiOperation(value = "Search a product with the name", response = ProductInfo.class)
    ResponseEntity<?> getProduct(@PathVariable("name") String productName) {

        Optional<Product> product = productService.getProductByName(productName);

        if (product.isPresent()) {
            return ResponseEntity.ok(ProductHelper.convertToProductInfo(product.get()));
        }

        return ResponseEntity.notFound()
                             .build();
    }

    @GetMapping("/products")
    @ApiOperation(value = "View  all products", response = ProductInfo.class, responseContainer="List")
    ResponseEntity<?> getProducts() {
        return ResponseEntity.ok(Optional.ofNullable(productService.getAllProducts()).orElseGet(ArrayList::new)
                                               .stream()
                                               .map(ProductHelper::convertToProductInfo)
                                               .collect(Collectors.toList()));
    }

    @PostMapping("/products")
    @ApiOperation(value = "Create a product")
    ResponseEntity<?> createProduct(@RequestBody @Valid ProductInfo productInfo, BindingResult bindingResult) {
        Optional<Product> existingProduct = productService.getProductByName(productInfo.getName());

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(bindingResult.getAllErrors()
                                                    .get(0)
                                                    .getDefaultMessage());
        }

        if (existingProduct.isPresent()) {
            LOGGER.info("Product with such name already exists!!!");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .build();
        }

        ProductInfo savedProductInfo = ProductHelper.convertToProductInfo(productService.save(productInfo));

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(savedProductInfo);
    }


    @PutMapping("/products/price")
    @ApiOperation(value = "Update current price of the product")
    ResponseEntity<?> updateProductCurrentPrice(@RequestBody @Valid PriceUpdateInfo priceUpdateInfo, BindingResult bindingResult) {
        Optional<Product> existingProduct = productService.getProductByName(priceUpdateInfo.getProductName());

        if (existingProduct.isPresent()) {
            NavigableSet<Price> nsPrices = PriceHelper.convertToNavigableSet(existingProduct.map(Product::getPrices).orElse(null));
            if (nsPrices.last().getFromDate().isAfter(priceUpdateInfo.getPrice().getFromDate()) || nsPrices.last()
                                                                                                           .getFromDate()
                                                                                                           .isEqual(priceUpdateInfo.getPrice().getFromDate())) {
                LOGGER.info("Product current price from date is newer or equal then date for new current price");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }


            existingProduct.get().getPrices().add(priceService.save(priceUpdateInfo));
            ProductInfo savedProductInfo = ProductHelper.convertToProductInfo(productService.save(existingProduct.get()));

            return ResponseEntity.ok(savedProductInfo);
        } else {
            LOGGER.info("Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
    }

    @GetMapping("/products/{name}/price")
    @ApiOperation(value = "Search current price of the product", response = Price.class)
    ResponseEntity<?> getCurrentPrice(@PathVariable("name") String productName) {

        Optional<Product> existingProduct = productService.getProductByName(productName);

        if (existingProduct.isPresent()) {
            if (existingProduct.get().getPrices() == null || existingProduct.get().getPrices().isEmpty()) {
                LOGGER.info("Current price not found");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(PriceHelper.convertToNavigableSet(existingProduct.map(Product::getPrices).orElse(null)).last());
        }

        LOGGER.info("Product not found");
        return ResponseEntity.notFound().build();
    }

}
