package com.example.productorder.product.domain;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductInfo {


    @NonNull
    @Size(min = 1, max = 255)
    private String name;

    private Set<Price> prices;
}