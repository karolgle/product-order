package com.example.productorder.product.service;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.PriceUpdateInfo;
import com.example.productorder.product.repository.PriceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.example.productorder.TestData.prepareProducts;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceServiceTest {

    @Autowired
    private PriceService priceService;

    @MockBean
    private PriceRepository priceRepository;

    private List<Price> testPrices;

    @Before
    public void setUp() {
        testPrices = new ArrayList<>(prepareProducts().get(0).getPrices());
    }

    @Test
    public void shouldReturnSavedPrice() {
        //given
        Price p = testPrices.get(0);
        PriceUpdateInfo priceUpdateInfo = PriceUpdateInfo.builder().price(p).productName(prepareProducts().get(0).getName()).build();
        given(priceRepository.save(p)).willReturn(p);

        //then
        assertThat(priceService.save(priceUpdateInfo)).isEqualTo(p);
    }
}