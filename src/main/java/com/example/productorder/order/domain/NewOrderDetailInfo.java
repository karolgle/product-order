package com.example.productorder.order.domain;


import lombok.*;


@Builder
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NewOrderDetailInfo {

    @NonNull
    private String productName;

    @NonNull
    private Long quantity;
}