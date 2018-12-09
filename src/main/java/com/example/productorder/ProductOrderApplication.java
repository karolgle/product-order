package com.example.productorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(
        basePackageClasses = {ProductOrderApplication.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class ProductOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductOrderApplication.class, args);
    }
}
