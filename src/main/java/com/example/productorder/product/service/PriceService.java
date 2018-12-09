package com.example.productorder.product.service;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.PriceUpdateInfo;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    @Autowired
    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price save(PriceUpdateInfo priceUpdateInfo) {
        return priceRepository.save(PriceHelper.convertToPrice(priceUpdateInfo));
    }


}
