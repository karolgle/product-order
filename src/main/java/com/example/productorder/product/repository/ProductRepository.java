package com.example.productorder.product.repository;

import com.example.productorder.product.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    List<Product> findAll();

    Optional<Product> findProductByName(String name);

    Optional<Product> findProductById(Long id);

    Long  countProductsByNameIsIn(Set<String> productsNames);
}
