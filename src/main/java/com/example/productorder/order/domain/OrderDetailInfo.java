package com.example.productorder.order.domain;


import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.ProductInfo;
import lombok.*;

import javax.persistence.ManyToOne;


@Builder
@ToString
@Getter
@NoArgsConstructor(force = true) //sets values to its defaults
@AllArgsConstructor
@EqualsAndHashCode
public class OrderDetailInfo {

    @NonNull
    @ManyToOne
    private ProductInfo product;

    @NonNull
    @ManyToOne
    private Price price;

    @NonNull
    private Long quantity;
}