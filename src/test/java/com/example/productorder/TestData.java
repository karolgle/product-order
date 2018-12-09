package com.example.productorder;

import com.example.productorder.order.domain.Order;
import com.example.productorder.order.domain.OrderDetail;
import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.helper.PriceHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class TestData {
    public static List<Product> prepareProducts(){
        List<Product> products = new ArrayList<>();
        Comparator<Price> priceComparator = (Price p1, Price p2) -> (p1.getFromDate().compareTo(p2.getFromDate()));

        Set<Price> pricesTreeSet1 = new TreeSet<>(priceComparator);
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(1950, 1, 1, 10, 30)).price(new BigDecimal(10.5)).build());
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(1975, 1, 1, 10, 30)).price(new BigDecimal(300.5)).build());
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(2000, 1, 1, 10, 30)).price(new BigDecimal(200.5)).build());

        products.add(Product.builder().id(1L).name("Product 1").prices(pricesTreeSet1).build());

        Set<Price> pricesTreeSet2 = new TreeSet<>(priceComparator);
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.of(1960, 1, 15, 0, 30)).price(new BigDecimal(20.5)).build());
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.of(1955, 1, 15, 0, 30)).price(new BigDecimal(400.5)).build());
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.of(2005, 1, 15, 0, 30)).price(new BigDecimal(500.5)).build());

        products.add(Product.builder().id(2L).name("Product 2").prices(pricesTreeSet2).build());

        Set<Price> pricesTreeSet3 = new TreeSet<>(priceComparator);
        pricesTreeSet3.add(Price.builder().fromDate(LocalDateTime.of(2005, 1, 15, 0, 30)).price(new BigDecimal(1000.55)).build());
        pricesTreeSet3.add(Price.builder().fromDate(LocalDateTime.of(2015, 1, 15, 0, 30)).price(new BigDecimal(5000.55)).build());
        pricesTreeSet3.add(Price.builder().fromDate(LocalDateTime.of(2025, 1, 15, 0, 30)).price(new BigDecimal(6000.33)).build());

        products.add(Product.builder().id(2L).name("Product 3").prices(pricesTreeSet3).build());

        return products;
    }

    public static List<Order> prepareOrders(List<Product> testProducts){

        List<Order> orders = new ArrayList<>();

        LocalDateTime orderDate = LocalDateTime.of(2015, 1, 15, 0, 30);
        Set<OrderDetail> orderDetails = new HashSet<>();
        orderDetails.add(OrderDetail.builder().product(testProducts.get(0)).quantity(10L).price(((NavigableSet<Price>)testProducts.get(0).getPrices()).last()).build());
        orderDetails.add(OrderDetail.builder().product(testProducts.get(0)).quantity(90L).price(((NavigableSet<Price>)testProducts.get(0).getPrices()).last()).build());
        orderDetails.add(OrderDetail.builder().product(testProducts.get(1)).quantity(5L).price(((NavigableSet<Price>)testProducts.get(1).getPrices()).last()).build());
        orders.add(Order.builder()
                        .email("customer1@cp.pc")
                        .orderDate(orderDate)
                        .orderDetail(orderDetails)
                        .build());

        LocalDateTime orderDate1 = LocalDateTime.of(2015, 1, 15, 0, 30);
        Price priceWithFakeAmount = Price.builder().fromDate(orderDate1).price(new BigDecimal(0)).build();
        Set<OrderDetail> orderDetails1 = new HashSet<>();
        orderDetails1.add(OrderDetail.builder().product(testProducts.get(2)).quantity(10L).price(((NavigableSet<Price>)testProducts.get(2).getPrices()).lower(priceWithFakeAmount)).build());
        orders.add(Order.builder()
                        .email("customer2@cp.pc")
                        .orderDate(orderDate1)
                        .orderDetail(orderDetails1)
                        .build());
        return orders;
    }
}
