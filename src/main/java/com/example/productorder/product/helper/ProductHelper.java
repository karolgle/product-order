package com.example.productorder.product.helper;

import com.example.productorder.product.domain.Product;
import com.example.productorder.product.domain.ProductInfo;

public class ProductHelper {

    public static Product convertToProduct(ProductInfo productInfo) {
        return Product.builder()
                      .name(productInfo.getName())
                      .prices(productInfo.getPrices())
                      .build();
    }

    public static ProductInfo convertToProductInfo(Product product) {
        return ProductInfo.builder()
                          .name(product.getName())
                          .prices(product.getPrices())
                          .build();
    }
}
