package com.example.productorder.product.domain;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@ToString
@Getter
@Entity(name = "PRICES")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Price {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private BigDecimal price;

    @NonNull
    private LocalDateTime fromDate;

}