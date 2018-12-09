package com.example.productorder.order.domain;


import lombok.*;

import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
public class OrderInfo {

    @NonNull
    @Email
    private String email;

    private LocalDateTime orderDate;

    Set<OrderDetailInfo> orderDetail;

    private BigDecimal orderSum;
}