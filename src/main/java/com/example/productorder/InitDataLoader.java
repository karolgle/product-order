package com.example.productorder;

import com.example.productorder.order.domain.Order;
import com.example.productorder.order.domain.OrderDetail;
import com.example.productorder.order.service.OrderService;
import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@ConditionalOnExpression("${init.data:true}")
public class InitDataLoader implements ApplicationRunner {

    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public InitDataLoader(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    public void run(ApplicationArguments args) {
        Set<Price> pricesTreeSet1 = new TreeSet<>((Price p1, Price p2) -> (p1.getFromDate().compareTo(p2.getFromDate())));
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(1999, 1, 1, 10, 30)).price(new BigDecimal(100.5)).build());
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(1989, 1, 1, 10, 30)).price(new BigDecimal(200.5)).build());
        pricesTreeSet1.add(Price.builder().fromDate(LocalDateTime.of(2000, 1, 1, 10, 30)).price(new BigDecimal(300.5)).build());


        productService.save(Product.builder()
                                   .name("Product 1")
                                   .prices(pricesTreeSet1)
                                   .build());

        Set<Price> pricesTreeSet2 = new TreeSet<>((Price p1, Price p2) -> (p1.getFromDate().compareTo(p2.getFromDate())));
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.now()).price(new BigDecimal(100.5)).build());
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.of(1989, 1, 1, 10, 30)).price(new BigDecimal(200.5)).build());
        pricesTreeSet2.add(Price.builder().fromDate(LocalDateTime.of(1977, 1, 1, 10, 30)).price(new BigDecimal(300.5)).build());

        productService.save(Product.builder()
                                   .name("Product 2")
                                   .prices(pricesTreeSet2)
                                   .build());


        List<Product> productList = productService.getAllProducts();
        NavigableSet<Price> nsPrices = new TreeSet<>(new TreeSet<>((Price p1, Price p2) -> (p1.getFromDate().compareTo(p2.getFromDate()))));
        nsPrices.addAll(productList.get(0).getPrices());
        nsPrices.toArray();


        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 10, 30);

        Set<OrderDetail> orderDetails = Collections.singleton(OrderDetail.builder()
                                                                         .product(productList.get(0))
                                                                         .price(nsPrices.lower(Price.builder()
                                                                                                    .price(new BigDecimal(0))
                                                                                                    .fromDate(localDateTime)
                                                                                                    .build()))
                                                                         .quantity(3L)
                                                                         .build());

        Order order = Order.builder()
                           .email("customer1@test.test")
                           .orderDate(localDateTime)
                           .orderDetail(orderDetails)
                           .build();

        orderService.save(order);

    }
}
