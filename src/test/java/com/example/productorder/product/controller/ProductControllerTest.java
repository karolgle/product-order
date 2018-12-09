package com.example.productorder.product.controller;

import com.example.productorder.product.domain.Price;
import com.example.productorder.product.domain.PriceUpdateInfo;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.domain.ProductInfo;
import com.example.productorder.product.helper.PriceHelper;
import com.example.productorder.product.helper.ProductHelper;
import com.example.productorder.product.service.PriceService;
import com.example.productorder.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.productorder.TestData.prepareProducts;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private PriceService priceService;

    @Autowired
    ProductController productController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private List<Product> testProducts;


    @Before
    public void setUp() {
        testProducts = prepareProducts();
    }

    @Test
    public void shouldCreateProduct() throws Exception {

        // given
        given(productService.getProductByName(any(String.class))).willReturn(Optional.empty());
        given(productService.save(any(ProductInfo.class))).willReturn(testProducts.get(0));
        ProductInfo productInfo = ProductHelper.convertToProductInfo(testProducts.get(0));

        // then
        mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(productInfo)))
           .andExpect(status().isCreated());

        verify(productService, times(1)).save(productInfo);
        verify(productService, times(1)).getProductByName(productInfo.getName());
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnConflictWhenProductAlreadyExists() throws Exception {

        // given
        given(productService.getProductByName(any(String.class))).willReturn(Optional.of(testProducts.get(0)));
        given(productService.save(any(ProductInfo.class))).willReturn(testProducts.get(0));
        ProductInfo productInfo = ProductHelper.convertToProductInfo(testProducts.get(0));

        // then
        mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(productInfo)))
           .andExpect(status().isConflict());

        verify(productService, times(0)).save(productInfo);
        verify(productService, times(1)).getProductByName(productInfo.getName());
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldGetOneProductByName() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(testProduct.getName())).willReturn(Optional.of(testProduct));
        ProductInfo productInfo = ProductHelper.convertToProductInfo(testProduct);

        // then
        mvc.perform(get("/products/{name}", testProduct.getName()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name", is(productInfo.getName())));

        verify(productService, times(1)).getProductByName(productInfo.getName());
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldGetZeroProductByName() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(testProduct.getName())).willReturn(Optional.empty());
        ProductInfo productInfo = ProductHelper.convertToProductInfo(testProduct);

        // then
        mvc.perform(get("/products/{name}", testProduct.getName()))
           .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductByName(productInfo.getName());
        verifyNoMoreInteractions(productService);
    }


    @Test
    public void shouldReturnAllProducts() throws Exception {

        // given
        given(productService.getAllProducts()).willReturn(testProducts);

        // then
        mvc.perform(get("/products"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].name", is(testProducts.get(0).getName())))
           .andExpect(jsonPath("$[1].name", is(testProducts.get(1).getName())))
           .andExpect(jsonPath("$[0].prices[*].price", hasItem(testProducts.get(0).getPrices().stream().findFirst().get().getPrice().doubleValue())))
           .andExpect(jsonPath("$[1].prices[*].price", hasItem(testProducts.get(1).getPrices().stream().findFirst().get().getPrice().doubleValue())));

        verify(productService, times(1)).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldNotReturnAnyProduct() throws Exception {

        // given
        given(productService.getAllProducts()).willReturn(null);

        // then
        mvc.perform(get("/products"))
           .andExpect(status().isOk());

        verify(productService, times(1)).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldUpdateCurrentPriceForProduct() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(any(String.class))).willReturn(Optional.of(testProduct));

        LocalDateTime currentPriceForTestProduct = PriceHelper.convertToNavigableSet(testProduct.getPrices()).last().getFromDate();
        PriceUpdateInfo newCurrentPrice = PriceUpdateInfo.builder()
                                                         .price(Price.builder()
                                                                     .price(new BigDecimal(1000))
                                                                     .fromDate(currentPriceForTestProduct.plusDays(1L))
                                                                     .build())
                                                         .productName(testProduct.getName())
                                                         .build();

        given(priceService.save(any(PriceUpdateInfo.class))).willReturn(PriceHelper.convertToPrice(newCurrentPrice));
        given(productService.save(any(Product.class))).willReturn(testProducts.get(0));
        // then
        mvc.perform(put("/products/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newCurrentPrice)))
           .andExpect(status().isOk());

        verify(productService, times(1)).getProductByName(testProduct.getName());
        verify(productService, times(1)).save(testProduct);
        verify(priceService, times(1)).save(newCurrentPrice);
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(priceService);
    }

    @Test
    public void shouldReturnBadRequestCurrentPriceOlderThenNewPrice() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(any(String.class))).willReturn(Optional.of(testProduct));

        LocalDateTime currentPriceForTestProduct = PriceHelper.convertToNavigableSet(testProduct.getPrices()).last().getFromDate();
        PriceUpdateInfo newCurrentPrice = PriceUpdateInfo.builder()
                                                         .price(Price.builder()
                                                                     .price(new BigDecimal(1000))
                                                                     .fromDate(currentPriceForTestProduct.minusDays(1L))
                                                                     .build())
                                                         .productName(testProduct.getName())
                                                         .build();

        // then
        mvc.perform(put("/products/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newCurrentPrice)))
           .andExpect(status().isBadRequest());

        verify(productService, times(1)).getProductByName(testProduct.getName());

        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(priceService);
    }

    @Test
    public void shouldReturnNotFoundIfProductNotExists() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(any(String.class))).willReturn(Optional.empty());

        LocalDateTime currentPriceForTestProduct = PriceHelper.convertToNavigableSet(testProduct.getPrices()).last().getFromDate();
        PriceUpdateInfo newCurrentPrice = PriceUpdateInfo.builder()
                                                         .price(Price.builder()
                                                                     .price(new BigDecimal(1000))
                                                                     .fromDate(currentPriceForTestProduct.minusDays(1L))
                                                                     .build())
                                                         .productName(testProduct.getName())
                                                         .build();

        // then
        mvc.perform(put("/products/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newCurrentPrice)))
           .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductByName(testProduct.getName());

        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(priceService);
    }

    @Test
    public void shouldGetCurrentPriceForProductByName() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(testProduct.getName())).willReturn(Optional.of(testProduct));

        Price currentPrice = PriceHelper.convertToNavigableSet(testProduct.getPrices()).last();

        // then
        mvc.perform(get("/products/{name}/price", testProduct.getName()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.price", is(currentPrice.getPrice().doubleValue())));

        verify(productService, times(1)).getProductByName(testProduct.getName());
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnNotFoundIfProductNotExistsForPrice() throws Exception {

        // given
        Product testProduct = testProducts.get(0);
        given(productService.getProductByName(testProduct.getName())).willReturn(Optional.empty());

        // then
        mvc.perform(get("/products/{name}/price", testProduct.getName()))
           .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductByName(testProduct.getName());
        verifyNoMoreInteractions(productService);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}