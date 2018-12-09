package com.example.productorder.order.controller;

import com.example.productorder.order.domain.*;
import com.example.productorder.order.helper.OrderHelper;
import com.example.productorder.order.service.OrderService;
import com.example.productorder.product.domain.Price;
import com.example.productorder.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/")
@Api(value="Orders", description="Operations pertaining to Orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/orders")
    @ApiOperation(value = "View  all orders between from and to dates", response = OrderInfo.class, responseContainer="List")
    ResponseEntity<?> getOrders(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false, value = "fromDate") LocalDateTime fromDate
            , @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false, value = "toDate") LocalDateTime toDate
    ) {

        //to date cannot be before from date
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            LOGGER.info("DateFrom is greater then dateTo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .build();
        }

        return ResponseEntity.ok(orderService.getOrders(fromDate, toDate)
                                             .stream()
                                             .map(order -> OrderHelper.convertToOrderInfo(order, orderService.countOrderTotal(order.getOrderDetail())))
                                             .collect(Collectors.toList()));
    }

    @PostMapping("/orders")
    @ApiOperation(value = "Create an order")
    ResponseEntity<?> createOrder(@RequestBody @Valid NewOrderInfo newOrderInfo, BindingResult bindingResult) {

        LocalDateTime orderDate = LocalDateTime.now();

        Optional<Order> existingOrder = orderService.getOrderByEmailAndDate(newOrderInfo.getEmail(), orderDate);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (existingOrder.isPresent()) {
            LOGGER.info("Order with such customer email and date already exists!!!");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .build();
        }

        if (!productService.checkIfAllProductsExists(newOrderInfo.getProductsToOrder()
                                                                 .stream()
                                                                 .map(NewOrderDetailInfo::getProductName)
                                                                 .collect(Collectors.toSet()))) {
            LOGGER.info("At least one of the ordered products does not exist!!!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .build();
        }

        Order savedOrderToSave = orderService.createNewOrder(newOrderInfo, orderDate);
        OrderInfo savedOrderInfo = OrderHelper.convertToOrderInfo(orderService.save(savedOrderToSave), orderService.countOrderTotal(savedOrderToSave.getOrderDetail()));

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrderInfo);
    }

    @GetMapping("/orders/{email}/{orderDate}/placed/{date}")
    @ApiOperation(value = "Check how order with specific email and order date would look(e.g. total order sum) if it would be placed in other date", response = OrderInfo.class)
    ResponseEntity<?> getOrderIfItWasPlacedOnSpecificDate(@Valid @PathVariable("email") String email
            , @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @Valid @PathVariable("orderDate") LocalDateTime orderDate
            , @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @Valid @PathVariable("date") LocalDateTime date) {

        Optional<Order> existingOrder = orderService.getOrderByEmailAndDate(email, orderDate);

        if (!existingOrder.isPresent()) {
            LOGGER.info("Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        // check if all products have price that can be used to count the "what if" order with specific orderDate
        if (!productService.checkIfAllProductsHavePriceOnDate(Stream.of(existingOrder.get())
                                                                    .flatMap(order -> order.getOrderDetail().stream())
                                                                    .map(OrderDetail::getProduct)
                                                                    .collect(Collectors.toSet()), date)) {
            LOGGER.info("At least one product does not have price in parameter date");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .build();
        }
        return ResponseEntity.ok(OrderHelper.convertToOrderInfo(existingOrder.get(), orderService.countOrderTotalIfItWasPlacedOnSpecificDate(existingOrder.get(), date)));
    }

}
