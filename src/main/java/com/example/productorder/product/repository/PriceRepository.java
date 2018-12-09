package com.example.productorder.product.repository;

import com.example.productorder.product.domain.Price;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Price, Integer> {
}
