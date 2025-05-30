package com.ssg.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.order.controller.dto.OrderCreateRequestDTO;
import com.ssg.order.service.OrderService;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_success() throws Exception{
        List<OrderCreateRequestDTO> requestBody = List.of(
                OrderCreateRequestDTO.builder()
                        .ordQty(5L)
                        .prdId(1000000001L)
                        .build(),
                OrderCreateRequestDTO.builder()
                        .ordQty(10L)
                        .prdId(1000000002L)
                        .build()
        );

        OrderDTO response = OrderDTO.builder()
                .ordId(1L)
                .orderItems(List.of(OrderItemDTO.builder()
                                .ordItemId(1L)
                                .ordQty(5L)
                                .ordItemSt("00")
                                .product(ProductDTO.builder()
                                        .prdId(1000000001L)
                                        .prdNm("이마트 생수")
                                        .stdUprc(BigDecimal.valueOf(800))
                                        .dcAmt(BigDecimal.valueOf(100))
                                        .build())
                        .build(),
                        OrderItemDTO.builder()
                                .ordItemId(2L)
                                .ordQty(10L)
                                .ordItemSt("00")
                                .product(ProductDTO.builder()
                                        .prdId(1000000002L)
                                        .prdNm("신라면 멀티팩")
                                        .stdUprc(BigDecimal.valueOf(4200))
                                        .dcAmt(BigDecimal.valueOf(500))
                                        .build())
                                .build()
                ))
                .build();

        Mockito.when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordId").value(1))
                .andExpect(jsonPath("$.totOrdAmt").value(40500))
                .andExpect(jsonPath("$.orderItems.length()").value(2))
                .andExpect(jsonPath("$.orderItems[0].ordItemId").value(1))
                .andExpect(jsonPath("$.orderItems[0].ordQty").value(5))
                .andExpect(jsonPath("$.orderItems[0].product.prdNm").value("이마트 생수"))
                .andExpect(jsonPath("$.orderItems[1].product.prdNm").value("신라면 멀티팩"));;


    }

    @Test
    void searchOrder_success() throws Exception {
        OrderItemDTO item = OrderItemDTO.builder()
                .ordItemId(1L)
                .ordQty(1L)
                .ordItemSt("00")
                .product(ProductDTO.builder()
                        .prdId(2L)
                        .prdNm("상품B")
                        .dcAmt(BigDecimal.valueOf(500))
                        .stdUprc(BigDecimal.valueOf(4000))
                        .build())
                .build();

        OrderDTO orderDTO = OrderDTO.builder()
                .ordId(10L)
                .orderItems(List.of(item))
                .build();

        Mockito.when(orderService.findByOrdId(10L)).thenReturn(orderDTO);

        mockMvc.perform(get("/v1/order/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordId").value(10L))
                .andExpect(jsonPath("$.orderItems[0].product.prdNm").value("상품B"))
                .andExpect(jsonPath("$.totOrdAmt").value(3500)); // (4000-500)*1
    }

    @Test
    void cancelOrder_success() throws Exception {
        ProductDTO product = ProductDTO.builder()
                .prdId(3L)
                .prdNm("상품A")
                .dcAmt(BigDecimal.valueOf(300))
                .stdUprc(BigDecimal.valueOf(3000))
                .build();

        OrderItemDTO canceledItem = OrderItemDTO.builder()
                .ordItemId(2L)
                .ordQty(2L)
                .ordItemSt("01")
                .product(product)
                .build();

        OrderItemDTO remainingItem = OrderItemDTO.builder()
                .ordItemId(3L)
                .ordQty(1L)
                .ordItemSt("00")
                .product(ProductDTO.builder()
                        .prdId(4L)
                        .prdNm("상품B")
                        .dcAmt(BigDecimal.valueOf(0))
                        .stdUprc(BigDecimal.valueOf(2000))
                        .build())
                .build();

        OrderDTO orderDTO = OrderDTO.builder()
                .ordId(20L)
                .orderItems(List.of(canceledItem, remainingItem))
                .build();

        Mockito.when(orderService.cancelOrder(20L, 3L)).thenReturn(orderDTO);

        mockMvc.perform(put("/v1/order/20/product/3/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rfnAmt").value(5400)) // (3000-300)*2
                .andExpect(jsonPath("$.totOrdAmt").value(2000))
                .andExpect(jsonPath("$.product.prdNm").value("상품A"));
    }
}
