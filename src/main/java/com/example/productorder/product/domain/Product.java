package com.example.productorder.product.domain;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;


@Builder
@ToString
@Getter
@Entity(name = "PRODUCTS")
@NoArgsConstructor(force = true) //sets values to its defaults
@AllArgsConstructor
@EqualsAndHashCode
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(unique = true)
    @Size(min = 1, max = 255)
    private final String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, orphanRemoval = true )
    private Set<Price> prices;

}