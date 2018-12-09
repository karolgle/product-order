package com.example.productorder.order.repository;

import com.example.productorder.order.domain.Order;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Integer> {

    List<Order> findAll();
    List<Order>  findByOrderDateBetween(LocalDateTime from, LocalDateTime to);
    List<Order>  findByOrderDateGreaterThanEqual(LocalDateTime from);
    List<Order>  findByOrderDateLessThanEqual(LocalDateTime to);

    Optional<Order> findByEmailAndOrderDate(String email, LocalDateTime date);
}
