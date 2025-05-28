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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderDTO findByOrdId(Long ordId){
        Order order = orderRepository.findByOrdId(ordId);

        OrderDTO result = OrderDTO.builder()
                .ordId(order.getOrdId())
                .orderItems(new ArrayList<>())
                .build();

        if(result == null){
            throw new OrderException("조회된 주문이 없습니다.");
        }

        return orderEntityToDTO(order.getOrdId(), order.getOrderItems());
    }

    public OrderDTO createOrder(List<OrderItemDTO> orderItems){

        List<Long> prdIds = orderItems.stream()
                .map(dto -> dto.product().prdId())
                .distinct()
                .toList();

        Map<Long, Product> productMap = productRepository.findAllByPrdIdIn(prdIds).stream()
                .collect(Collectors.toMap(Product::getPrdId, Function.identity()));

        Order order = Order.builder()
                .orderItems(new ArrayList<>())
                .build();

        for(OrderItemDTO item : orderItems){
            Product product = productMap.get(item.product().prdId());

            if (product == null) {
                throw new OrderException("상품 ID " + item.product().prdId() + "에 해당하는 상품이 없습니다.");
            }

            long currentStock = product.getStkQty();
            long orderQty = item.ordQty();

            if(currentStock < orderQty){
                throw new OrderException("상품 " + product.getPrdNm() + "의 재고가 부족합니다.");
            }

            product.setStkQty(currentStock - orderQty);

            OrderItem addItem = OrderItem.builder()
                    .ordQty(item.ordQty())
                    .prdNm(product.getPrdNm())
                    .prdId(product.getPrdId())
                    .stdUprc(product.getStdUprc())
                    .dcAmt(product.getDcAmt())
                    .ordItemSt("00")
                    .build();

            order.addOrderItem(addItem);
        }

        Order saved = orderRepository.save(order);

        return orderEntityToDTO(saved.getOrdId(), saved.getOrderItems());

    }

    private OrderDTO orderEntityToDTO(Long ordId, List<OrderItem> orderItems){

        OrderDTO result = OrderDTO.builder()
                .ordId(ordId)
                .orderItems(new ArrayList<>())
                .build();

        for(OrderItem item : orderItems){
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
