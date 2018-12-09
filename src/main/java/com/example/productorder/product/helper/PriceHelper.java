package com.example.productorder.product.helper;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.PriceUpdateInfo;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class PriceHelper {

    public static NavigableSet<Price> convertToNavigableSet(Set<Price> priceSet) {
        return convertToNavigableSet(priceSet, ((p1, p2) -> (p1.getFromDate().compareTo(p2.getFromDate()))));
    }

    private static NavigableSet<Price> convertToNavigableSet(Set<Price> set, Comparator<Price> comparator) {
        NavigableSet<Price> navigableSet = new TreeSet<>(new TreeSet<>(comparator));
        if (set != null && !set.isEmpty()) {
            navigableSet.addAll(set);
        }

        return navigableSet;
    }

    public static Price convertToPrice(PriceUpdateInfo priceUpdateInfo) {
        return Price.builder().price(priceUpdateInfo.getPrice().getPrice()).fromDate(priceUpdateInfo.getPrice().getFromDate()).build();
    }


}
