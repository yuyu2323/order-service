package com.ssg.order.service;

import com.ssg.order.common.enums.OrderStatus;
import com.ssg.order.entity.Order;
import com.ssg.order.entity.OrderItem;
import com.ssg.order.entity.Product;
import com.ssg.order.exception.OrderException;
import com.ssg.order.repository.OrderRepository;
import com.ssg.order.repository.ProductRepository;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Transactional(readOnly = true)
    public OrderDTO findByOrdId(Long ordId){
        if(ordId ==  null){
            throw new OrderException("주문 검색 필수값 확인이 필요합니다.");
        }

        Order order = orderRepository.findByOrdId(ordId)
                .orElseThrow(() -> new OrderException("주문 조회를 실패하였습니다."));;

        return orderEntityToDTO(order.getOrdId(), order.getOrderItems());
    }

    @Transactional
    public OrderDTO createOrder(List<OrderItemDTO> orderItems){

        if(orderItems ==  null || orderItems.isEmpty()){
            throw new OrderException("주문 생성 아이템이 비었습니다.");
        }

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
                    .ordItemSt(OrderStatus.CREATED.getCode())
                    .build();

            order.addOrderItem(addItem);
        }

        Order saved = orderRepository.save(order);

        return orderEntityToDTO(saved.getOrdId(), saved.getOrderItems());

    }

    @Transactional
    public OrderDTO cancelOrder(Long ordId, Long prdId){

        if(ordId ==  null || prdId == null){
            throw new OrderException("주문 취소 필수값 확인이 필요합니다.");
        }

        Order order = orderRepository.findByOrdId(ordId)
                .orElseThrow(() -> new OrderException("주문 조회를 실패하였습니다."));

        Product product = productRepository.findByPrdId(prdId)
                .orElseThrow(() -> new OrderException("상품 조회를 실패하였습니다."));;

        for(OrderItem item : order.getOrderItems()){
            if(item.getPrdId().equals(prdId)){
                //이미 취소 처리 된 주문에 대한 검증
                if(OrderStatus.CANCELED.getCode().equals(item.getOrdItemSt())){
                    throw new OrderException("이미 취소된 주문아이템 입니다.");
                }

                //상태변경
                item.setOrdItemSt(OrderStatus.CANCELED.getCode());
                //재고 복구
                product.setStkQty(product.getStkQty() + item.getOrdQty());

            }
        }

        return orderEntityToDTO(order.getOrdId(), order.getOrderItems());
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
