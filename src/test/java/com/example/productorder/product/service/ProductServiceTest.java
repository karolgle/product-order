package com.example.productorder.product.service;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.domain.ProductInfo;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.helper.ProductHelper;
import com.example.productorder.product.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.productorder.TestData.prepareProducts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    private List<Product> testProducts;

    @Before
    public void setUp() throws Exception {
        testProducts = prepareProducts();
    }

    @Test
    public void shouldReturnSavedProduct() {
        //given
        Product p = testProducts.get(0);
        given(productRepository.save(p)).willReturn(p);

        //then
        assertThat(productService.save(p)).isEqualTo(p);
    }

    @Test
    public void shouldReturnSavedProduct2() {
        //given
        Product p = testProducts.get(0);
        ProductInfo pI = ProductHelper.convertToProductInfo(p);
        given(productRepository.save(any(Product.class))).willReturn(p);

        //then
        assertThat(productService.save(pI)).isEqualTo(p);
    }

    @Test
    public void shouldReturnAllProducts() {
        //given
        Product p = testProducts.get(0);
        given(productRepository.findAll()).willReturn(testProducts);

        //then
        assertThat(productService.getAllProducts()).containsAll(testProducts);
    }

    @Test
    public void shouldReturnOneProduct() {
        //given
        Product p = testProducts.get(0);
        given(productRepository.findProductByName(p.getName())).willReturn(Optional.of(p));

        //then
        assertThat(productService.getProductByName(p.getName()).get()).isEqualTo(p);
    }

    @Test
    public void shouldReturnZeroProducts() {
        //given
        given(productRepository.findProductByName("notExistingProduct")).willReturn(Optional.empty());

        //then
        assertThat(productService.getProductByName("notExistingProduct")).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnTrueWhenAllProductsExists() {
        //given
        Set<String> testProductsNames = new HashSet<>(testProducts.stream().map(product -> product.getName()).collect(Collectors.toSet()));
        Long testProductsSize = (long) testProducts.size();
        given(productRepository.countProductsByNameIsIn(testProductsNames)).willReturn(testProductsSize);

        //then
        assertThat(productService.checkIfAllProductsExists(testProductsNames)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenAnyOfTheProductsDoesNotExists() {
        //given
        Set<String> testProductsNames = new HashSet<>(testProducts.stream().map(product -> product.getName()).collect(Collectors.toSet()));
        Long testProductsSize = (long) testProducts.size();
        given(productRepository.countProductsByNameIsIn(testProductsNames)).willReturn(0L);

        //then
        assertThat(productService.checkIfAllProductsExists(testProductsNames)).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenPriceBeforeTheDateExists() {
        //given
        Set<Product> testProductsSet = new HashSet<>(testProducts);
        LocalDateTime dateFromExistingPrice = LocalDateTime.of(2010, 5, 1, 0, 0);

        //then
        assertThat(productService.checkIfAllProductsHavePriceOnDate(testProductsSet, dateFromExistingPrice)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenPriceBeforeTheDateDoesNotExist() {
        //given
        Set<Product> testProductsSet = new HashSet<>(testProducts);
        NavigableSet<Price> testPricesSet = PriceHelper.convertToNavigableSet(testProducts.get(0).getPrices());

        //then
        assertThat(productService.checkIfAllProductsHavePriceOnDate(testProductsSet, testPricesSet.first().getFromDate())).isFalse();
    }


}