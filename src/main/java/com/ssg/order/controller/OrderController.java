package com.ssg.order.controller;

import com.ssg.order.controller.dto.OrderCreateRequestDTO;
import com.ssg.order.controller.dto.OrderInfoResponseDTO;
import com.ssg.order.controller.dto.OrderItemInfoResponseDTO;
import com.ssg.order.controller.dto.ProductInfoResponseDTO;
import com.ssg.order.entity.OrderItem;
import com.ssg.order.service.OrderService;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{ordId}")
    public OrderInfoResponseDTO searchOrder(@PathVariable Long ordId){

        OrderDTO order = orderService.findByOrdId(ordId);

        OrderInfoResponseDTO result = OrderInfoResponseDTO.builder()
                .orderItems(new ArrayList<>())
                .ordId(order.ordId())
                .build();

        for(OrderItemDTO item : order.orderItems()){
            result.orderItems().add(
                    OrderItemInfoResponseDTO.builder()
                            .ordItemId(item.ordItemId())
                            .ordQty(item.ordQty())
                            .product(ProductInfoResponseDTO.builder()
                                    .prdId(item.product().prdId())
                                    .prdNm(item.product().prdNm())
                                    .dcAmt(item.product().dcAmt())
                                    .stdUprc(item.product().stdUprc())
                                    .build())
                            .build()
            );
        }

        return result;
    }

}
