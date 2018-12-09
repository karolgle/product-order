package com.example.productorder.order.helper;

import com.example.productorder.order.domain.Order;
import com.example.productorder.order.domain.OrderDetail;
import com.example.productorder.order.domain.OrderDetailInfo;
import com.example.productorder.order.domain.OrderInfo;
import com.example.productorder.product.helper.ProductHelper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class OrderHelper {

    public static Order convertToOrder(OrderInfo orderInfo) {
        return Order.builder()
                    .email(orderInfo.getEmail())
                    .orderDate(orderInfo.getOrderDate())
                    .orderDetail(orderInfo.getOrderDetail().stream().map(OrderHelper::convertToOrderDetail).collect(Collectors.toSet()))
                    .build();
    }

    public static OrderInfo convertToOrderInfo(Order order, BigDecimal orderSum) {
        return OrderInfo.builder()
                        .email(order.getEmail())
                        .orderDate(order.getOrderDate())
                        .orderDetail(order.getOrderDetail().stream().map(OrderHelper::convertToOrderDetailInfo).collect(Collectors.toSet()))
                        .orderSum(orderSum)
                        .build();
    }

    private static OrderDetail convertToOrderDetail(OrderDetailInfo orderDetailInfo) {
        return OrderDetail.builder()
                          .product(ProductHelper.convertToProduct(orderDetailInfo.getProduct()))
                          .quantity(orderDetailInfo.getQuantity())
                          .price(orderDetailInfo.getPrice())
                          .build();
    }

    private static OrderDetailInfo convertToOrderDetailInfo(OrderDetail orderDetail) {
        return OrderDetailInfo.builder()
                              .product(ProductHelper.convertToProductInfo(orderDetail.getProduct()))
                              .quantity(orderDetail.getQuantity())
                              .price(orderDetail.getPrice())
                              .build();
    }
}
