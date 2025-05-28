package com.ssg.order.service;

import com.ssg.order.entity.Order;
import com.ssg.order.entity.OrderItem;
import com.ssg.order.exception.OrderException;
import com.ssg.order.repository.OrderRepository;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderDTO findByOrdNo(Long ordNo){
        Order order = orderRepository.findByOrdNo(ordNo);

        OrderDTO result = OrderDTO.builder()
                .ordId(order.getOrdId())
                .orderItems(new ArrayList<>())
                .build();

        if(result == null){
            throw new OrderException("조회된 주문이 없습니다.");
        }

        for(OrderItem item : order.getOrderItems()){
            result.orderItems().add(
                    OrderItemDTO.builder()
                        .ordItemId(item.getOrdItemId())
                        .ordItemSt(item.getOrdItemSt())
                        .ordQty(item.getOrdQty())
                        .product(ProductDTO.builder()
                                .prdId(item.getPrdId())
                                .prdNm(item.getPrdNm())
                                .dcAmt(item.getDcAmt())
                                .stdUprc(item.getStdUprc())
                                .build())
                    .build()
            );
        }

        return result;
    }
}
