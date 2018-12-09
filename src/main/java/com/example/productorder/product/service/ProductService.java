package com.example.productorder.product.service;

import com.example.productorder.product.domain.Product;
import com.example.productorder.product.domain.ProductInfo;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.helper.ProductHelper;
import com.example.productorder.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(ProductInfo productInfo) {
        return this.save(ProductHelper.convertToProduct(productInfo));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductByName(String name) {
        return productRepository.findProductByName(name);
    }

    public boolean checkIfAllProductsExists(Set<String> productsNames) {
        //only one product with specific name should exist so we can compare counts
        return productRepository.countProductsByNameIsIn(productsNames) == productsNames.size();
    }

    public boolean checkIfAllProductsHavePriceOnDate(Set<Product> productSet, LocalDateTime date) {
        return productSet.stream()
                         .map(product -> PriceHelper.convertToNavigableSet(product.getPrices()))
                         .noneMatch(prices -> prices.first().getFromDate().isAfter(date));
    }
}
