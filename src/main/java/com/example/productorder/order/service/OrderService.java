package com.example.productorder.order.service;

import com.example.productorder.order.domain.NewOrderDetailInfo;
import com.example.productorder.order.domain.NewOrderInfo;
import com.example.productorder.order.domain.Order;
import com.example.productorder.order.domain.OrderDetail;
import com.example.productorder.order.repository.OrderRepository;
import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;


    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }


    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderByEmailAndDate(String email, LocalDateTime date) {
        return orderRepository.findByEmailAndOrderDate(email, date);
    }

    public List<Order> getOrders(LocalDateTime from, LocalDateTime to) {

        List<Order> orders;
        if (from != null && to != null) {
            orders = orderRepository.findByOrderDateBetween(from, to);
        } else if (from != null) {
            orders = orderRepository.findByOrderDateGreaterThanEqual(from);
        } else if (to != null) {
            orders = orderRepository.findByOrderDateLessThanEqual(to);
        } else {
            orders = orderRepository.findAll();
        }

        return orders;
    }

    public BigDecimal countOrderTotalIfItWasPlacedOnSpecificDate(Order order, LocalDateTime localDateTime) {
        Set<OrderDetail> orderDetailsWithPriceOnDate = order.getOrderDetail()
                                                            .stream()
                                                            .map(oDet -> OrderDetail.builder()
                                                                                     .price(Optional.ofNullable(getPriceClosestToDate(localDateTime, oDet.getProduct())).orElseThrow(() -> new IllegalArgumentException("Price for such date does not exist!!!")))
                                                                                     .product(oDet.getProduct())
                                                                                     .quantity(oDet.getQuantity())
                                                                                     .build())
                                                            .collect(Collectors.toSet());

        return countOrderTotal(orderDetailsWithPriceOnDate);
    }

    public BigDecimal countOrderTotal(Set<OrderDetail> orderDetails) {
        return orderDetails.stream().map(oD -> oD.getPrice().getPrice().multiply(new BigDecimal(oD.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    public Order createNewOrder(NewOrderInfo newOrderInfo, LocalDateTime orderDate) {
        Map<String, NewOrderDetailInfo> mergeNewOrderDetailInfo = newOrderInfo.getProductsToOrder().stream()
                                                                              .collect(Collectors.groupingBy(NewOrderDetailInfo::getProductName,
                                                                                      Collectors.collectingAndThen(
                                                                                              reduceNewOrderDetailInfoWithSameProductNames(), Optional::get)));

        Set<Product> uniqueProducts = mergeNewOrderDetailInfo.keySet()
                                                             .stream()
                                                             .map(productName -> productService.getProductByName(productName).orElse(null))
                                                             .collect(Collectors.toSet());

        Set<OrderDetail> orderDetailsToSave = mergeNewOrderDetailInfo.values()
                                                                     .stream()
                                                                     .map(nODI -> OrderDetail.builder()
                                                                                             .product(getProduct(uniqueProducts, nODI))
                                                                                             .price(Optional.ofNullable(getPriceClosestToDate(orderDate, getProduct(uniqueProducts, nODI))).orElseThrow(() -> new IllegalArgumentException("Price for such date does not exist!!!")))
                                                                                             .quantity(nODI.getQuantity())
                                                                                             .build())
                                                                     .collect(Collectors.toSet());

        return Order.builder()
                    .orderDate(orderDate)
                    .email(newOrderInfo.getEmail())
                    .orderDetail(orderDetailsToSave)
                    .build();

    }

    private Collector<NewOrderDetailInfo, ?, Optional<NewOrderDetailInfo>> reduceNewOrderDetailInfoWithSameProductNames() {
        return Collectors.reducing((a, b) -> NewOrderDetailInfo.builder()
                                                               .quantity(a.getQuantity() + b.getQuantity())
                                                               .productName(a.getProductName())
                                                               .build());
    }

    private Price getPriceClosestToDate(LocalDateTime date, Product product) {
        Price priceWithFakeAmount = Price.builder().fromDate(date).price(new BigDecimal(0)).build();
        return PriceHelper.convertToNavigableSet(product.getPrices()).lower(priceWithFakeAmount);
    }

    private Product getProduct(Set<Product> uniqueProducts, NewOrderDetailInfo nODI) {
        return uniqueProducts.stream()
                             .filter(product -> product.getName()
                                                       .equals(nODI.getProductName()))
                             .findFirst()
                             .orElse(null);
    }
}
