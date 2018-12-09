package com.example.productorder.order.service;

import com.example.productorder.order.domain.NewOrderDetailInfo;
import com.example.productorder.order.domain.NewOrderInfo;
import com.example.productorder.order.domain.Order;
import com.example.productorder.order.repository.OrderRepository;
import com.example.productorder.product.domain.Product;
import com.example.productorder.product.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.productorder.TestData.prepareOrders;
import static com.example.productorder.TestData.prepareProducts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductService productService;


    private List<Order> testOrders;

    @Before
    public void setUp() {
        List<Product> testProducts = prepareProducts();
        testOrders = prepareOrders(testProducts);
    }


    @Test
    public void shouldReturnSavedProduct() {
        //given
        Order o = testOrders.get(0);
        given(orderRepository.save(o)).willReturn(o);

        //then
        assertThat(orderService.save(o)).isEqualTo(o);
    }

    @Test
    public void shouldReturnAllOrders() {
        //given
        given(orderRepository.findAll()).willReturn(testOrders);
        //then
        assertThat(orderService.getAllOrders()).containsAll(testOrders);
    }

    @Test
    public void shouldReturnOneOrderByEmailAndDate() {
        //given
        Order o = testOrders.get(0);
        given(orderRepository.findByEmailAndOrderDate(o.getEmail(), o.getOrderDate())).willReturn(Optional.of(o));
        //then
        assertThat(orderService.getOrderByEmailAndDate(o.getEmail(), o.getOrderDate())).isEqualTo(Optional.of(o));
    }

    @Test
    public void shouldReturnZeroOrderByEmailAndDate() {
        //given
        Order o = testOrders.get(0);
        given(orderRepository.findByEmailAndOrderDate(o.getEmail(), o.getOrderDate())).willReturn(Optional.empty());
        //then
        assertThat(orderService.getOrderByEmailAndDate(o.getEmail(), o.getOrderDate())).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnOrdersBetween() {
        //given
        LocalDateTime from = LocalDateTime.of(2000, 1, 15, 0, 30);
        LocalDateTime to = LocalDateTime.of(2015, 1, 15, 0, 30);

        given(orderRepository.findByOrderDateBetween(from, to)).willReturn(testOrders);
        given(orderRepository.findByOrderDateGreaterThanEqual(from)).willReturn(testOrders);
        given(orderRepository.findByOrderDateLessThanEqual(to)).willReturn(testOrders);
        given(orderRepository.findAll()).willReturn(testOrders);

        //then
        assertThat(orderService.getOrders(from, to)).isEqualTo(testOrders);
        assertThat(orderService.getOrders(from, null)).isEqualTo(testOrders);
        assertThat(orderService.getOrders(null, to)).isEqualTo(testOrders);
        assertThat(orderService.getOrders(null, null)).isEqualTo(testOrders);

    }

    @Test
    public void shouldCountOrderTotalIfItWasPlacedOnSpecificDate() {
        //given
        Order o = testOrders.get(1);
        LocalDateTime date = LocalDateTime.of(2015, 1, 15, 0, 30);

        //then
        assertThat(orderService.countOrderTotalIfItWasPlacedOnSpecificDate(o, date)).isEqualTo(new BigDecimal(10005.5).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void shouldCountOrderTotal() {
        //given
        Order o = testOrders.get(1);

        //then
        assertThat(orderService.countOrderTotal(o.getOrderDetail())).isEqualTo(new BigDecimal(10005.50).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void shouldCreateNewOrder() {
        //given
        Order o = testOrders.get(1);
        List<NewOrderDetailInfo> newOrderDetailInfos = o.getOrderDetail()
                                                        .stream()
                                                        .map(orderDetail -> NewOrderDetailInfo.builder()
                                                                                              .productName(orderDetail.getProduct().getName())
                                                                                              .quantity(o.getOrderDetail().stream().findFirst().get().getQuantity())
                                                                                              .build())
                                                        .collect(Collectors.toList());

        NewOrderInfo newOrderInfo = NewOrderInfo.builder()
                                                .email(o.getEmail())
                                                .productsToOrder(newOrderDetailInfos)
                                                .build();

        LocalDateTime orderDate = LocalDateTime.of(2015, 1, 15, 0, 30);

        given(productService.getProductByName(any(String.class))).willReturn(Optional.of(o.getOrderDetail().stream().findFirst().get().getProduct()));

        //then
        assertThat(orderService.createNewOrder(newOrderInfo, orderDate)).isEqualTo(o);
    }


}