package com.example.productorder.order.domain;


import lombok.*;

import javax.validation.constraints.Email;
import java.util.List;


@Builder
@ToString
@Getter
@NoArgsConstructor(force = true) //sets values to its defaults
@AllArgsConstructor
@EqualsAndHashCode
public class NewOrderInfo {

    @NonNull
    @Email
    private final String email;

    private List<NewOrderDetailInfo> productsToOrder;

}