package com.example.productorder.product.domain;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PriceUpdateInfo {


    @NonNull
    private String productName;

    @NonNull
    private Price price;
}