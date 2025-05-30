package com.ssg.order.service;

import com.ssg.order.entity.Order;
import com.ssg.order.entity.OrderItem;
import com.ssg.order.entity.Product;
import com.ssg.order.exception.OrderException;
import com.ssg.order.repository.OrderRepository;
import com.ssg.order.repository.ProductRepository;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByOrdId_success() {
        Long orderId = 1L;

        OrderItem orderItem = OrderItem.builder()
                .ordItemId(10L)
                .ordItemSt("00")
                .ordQty(2L)
                .prdId(100L)
                .prdNm("Test Product")
                .stdUprc(BigDecimal.valueOf(1000))
                .dcAmt(BigDecimal.valueOf(100L))
                .build();

        Order order = Order.builder()
                .ordId(orderId)
                .orderItems(List.of(orderItem))
                .build();

        when(orderRepository.findByOrdId(orderId)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.findByOrdId(orderId);

        assertThat(dto).isNotNull();
        assertThat(dto.ordId()).isEqualTo(orderId);
        assertThat(dto.orderItems()).hasSize(1);
        assertThat(dto.orderItems().get(0).product().prdNm()).isEqualTo("Test Product");
    }

    @Test
    void testCreateOrder_productNotFound() {
        OrderItemDTO orderItemDTO = OrderItemDTO.builder()
                .ordQty(1L)
                .product(ProductDTO.builder().prdId(999L).build())
                .build();

        when(productRepository.findAllByPrdIdIn(List.of(999L))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderService.createOrder(List.of(orderItemDTO)))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("상품 ID 999에 해당하는 상품이 없습니다.");
    }

    @Test
    void testCancelOrder_success() {
        Long ordId = 1L;
        Long prdId = 100L;

        OrderItem orderItem = OrderItem.builder()
                .ordItemId(10L)
                .prdId(prdId)
                .ordItemSt("00")  // 정상 상태
                .ordQty(2L)
                .build();

        Order order = Order.builder()
                .ordId(ordId)
                .orderItems(List.of(orderItem))
                .build();

        Product product = Product.builder()
                .prdId(prdId)
                .prdNm("Product A")
                .stkQty(5L)
                .build();

        when(orderRepository.findByOrdId(ordId)).thenReturn(Optional.of(order));
        when(productRepository.findByPrdId(prdId)).thenReturn(Optional.of(product));

        OrderDTO result = orderService.cancelOrder(ordId, prdId);

        assertThat(result.orderItems()).hasSize(1);
        assertThat(result.orderItems().get(0).ordItemSt()).isEqualTo("01"); // 상태 취소됨
        assertThat(product.getStkQty()).isEqualTo(7L); // 재고 복구 확인
    }

    @Test
    void testCancelOrder_alreadyCancelled() {
        Long ordId = 1L;
        Long prdId = 100L;

        OrderItem orderItem = OrderItem.builder()
                .ordItemId(10L)
                .prdId(prdId)
                .ordItemSt("01")  // 이미 취소된 상태
                .ordQty(2L)
                .build();

        Order order = Order.builder()
                .ordId(ordId)
                .orderItems(List.of(orderItem))
                .build();

        Product product = Product.builder()
                .prdId(prdId)
                .prdNm("Product A")
                .stkQty(5L)
                .build();

        when(orderRepository.findByOrdId(ordId)).thenReturn(Optional.of(order));
        when(productRepository.findByPrdId(prdId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.cancelOrder(ordId, prdId))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("이미 취소된 주문아이템 입니다.");
    }
}
