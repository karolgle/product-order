package com.example.productorder.order.controller;

import com.example.productorder.order.domain.NewOrderDetailInfo;
import com.example.productorder.order.domain.NewOrderInfo;
import com.example.productorder.order.domain.Order;
import com.example.productorder.order.service.OrderService;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.service.PriceService;
import com.example.productorder.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.productorder.TestData.prepareOrders;
import static com.example.productorder.TestData.prepareProducts;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    @MockBean
    private PriceService priceService;

    @Autowired
    OrderController orderController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private List<Order> testOrders;

    @Before
    public void setUp() {
        List<Product> testProducts = prepareProducts();
        testOrders = prepareOrders(testProducts);
    }

    @Test
    public void shouldReturnOrdersWithFromParam() throws Exception {

        //given
        LocalDateTime from = LocalDateTime.of(2000, 1, 15, 0, 30);
        given(orderService.getOrders(from, null)).willReturn(testOrders);

        // then
        mvc.perform(get("/orders")
                .param("fromDate", "2000-01-15T00:30:00"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].email", is(testOrders.get(0).getEmail())))
           .andExpect(jsonPath("$[1].email", is(testOrders.get(1).getEmail())));


        verify(orderService, times(1)).getOrders(from, null);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnOrdersWithToParam() throws Exception {

        //given
        LocalDateTime to = LocalDateTime.of(2015, 1, 15, 0, 30);
        given(orderService.getOrders(null, to)).willReturn(testOrders);

        // then
        mvc.perform(get("/orders")
                .param("toDate", "2015-01-15T00:30:00"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].email", is(testOrders.get(0).getEmail())))
           .andExpect(jsonPath("$[1].email", is(testOrders.get(1).getEmail())));


        verify(orderService, times(1)).getOrders(null, to);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnOrdersWithFromToParam() throws Exception {

        //given
        LocalDateTime from = LocalDateTime.of(2000, 1, 15, 0, 30);
        LocalDateTime to = LocalDateTime.of(2015, 1, 15, 0, 30);
        given(orderService.getOrders(from, to)).willReturn(testOrders);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("fromDate", Lists.newArrayList("2000-01-15T00:30:00"));
        params.addAll("toDate", Lists.newArrayList("2015-01-15T00:30:00"));

        // then
        mvc.perform(get("/orders")
                .params(params))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].email", is(testOrders.get(0).getEmail())))
           .andExpect(jsonPath("$[1].email", is(testOrders.get(1).getEmail())));


        verify(orderService, times(1)).getOrders(from, to);
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnBadRequestWhenMixedParams() throws Exception {

        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("fromDate", Lists.newArrayList("2015-01-15T00:30:00"));
        params.addAll("toDate", Lists.newArrayList("2000-01-15T00:30:00"));

        // then
        mvc.perform(get("/orders")
                .params(params))
           .andExpect(status().isBadRequest());


        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldCreateOrder() throws Exception {

        //given
        Order testOrder = testOrders.get(1);
        NewOrderDetailInfo newOrderDetailInfo = NewOrderDetailInfo.builder().quantity(testOrder.getOrderDetail().stream().findFirst().get().getQuantity())
                                                                  .productName(testOrder.getOrderDetail().stream().findFirst().get().getProduct().getName())
                                                                  .build();
        NewOrderInfo newOrderInfo = NewOrderInfo.builder()
                                                .email(testOrder.getEmail())
                                                .productsToOrder(Collections.singletonList(newOrderDetailInfo))
                                                .build();
        //noinspection unchecked
        given(productService.checkIfAllProductsExists(any(Set.class))).willReturn(true);
        given(orderService.save(any(Order.class))).willReturn(testOrder);
        given(orderService.createNewOrder(any(NewOrderInfo.class), any(LocalDateTime.class))).willReturn(testOrder);
        // then
        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newOrderInfo)))
           .andExpect(status().isCreated());

        //noinspection unchecked
        verify(productService, times(1)).checkIfAllProductsExists(any(Set.class));
        verify(orderService, times(1)).save(any(Order.class));
        verifyNoMoreInteractions(productService);
    }

    @Test
    public void shouldReturnBadRequestWhenIncorrectEmail() throws Exception {

        //given
        Order testOrder = testOrders.get(1);
        NewOrderDetailInfo newOrderDetailInfo = NewOrderDetailInfo.builder().quantity(testOrder.getOrderDetail().stream().findFirst().get().getQuantity())
                                                                  .productName(testOrder.getOrderDetail().stream().findFirst().get().getProduct().getName())
                                                                  .build();
        NewOrderInfo newOrderInfo = NewOrderInfo.builder()
                                                .email("incorrectEmail")
                                                .productsToOrder(Collections.singletonList(newOrderDetailInfo))
                                                .build();
        given(orderService.save(any(Order.class))).willReturn(testOrder);
        given(orderService.createNewOrder(any(NewOrderInfo.class), any(LocalDateTime.class))).willReturn(testOrder);
        // then
        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newOrderInfo)))
           .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("must be a well-formed email address")));

        verifyNoMoreInteractions(productService);
    }

    @Test
    public void getOrderIfItWasPlacedOnSpecificDate() throws Exception {
        //given
        Order testOrder = testOrders.get(1);
        LocalDateTime orderDate = LocalDateTime.of(2015, 1, 15, 0, 30);
        given(orderService.getOrderByEmailAndDate(testOrder.getEmail(), orderDate)).willReturn(Optional.of(testOrder));
        //noinspection unchecked
        given(productService.checkIfAllProductsHavePriceOnDate(any(Set.class),any(LocalDateTime.class))).willReturn(true);

        // then
        mvc.perform(get("/orders/{email}/{orderDate}/placed/{date}", testOrder.getEmail(), "2015-01-15T00:30:00", "2000-01-15T00:30:00"))
           .andExpect(status().isOk());

        verify(orderService, times(1)).getOrderByEmailAndDate(testOrder.getEmail(), orderDate);
        //noinspection unchecked
        verify(productService, times(1)).checkIfAllProductsHavePriceOnDate(any(Set.class),any(LocalDateTime.class));
        verify(orderService, times(1)).countOrderTotalIfItWasPlacedOnSpecificDate(any(Order.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(orderService);
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