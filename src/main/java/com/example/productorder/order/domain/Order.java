package com.example.productorder.order.domain;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Set;


@Builder
@ToString
@Getter
@Entity(name = "ORDERS")
@NoArgsConstructor(force = true) //sets values to its defaults
@AllArgsConstructor
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Email
    private final String email;

    @NonNull
    private final LocalDateTime orderDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true )
    Set<OrderDetail> orderDetail;

}