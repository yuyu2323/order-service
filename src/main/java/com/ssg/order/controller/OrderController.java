package com.ssg.order.controller;

import com.ssg.order.controller.dto.*;
import com.ssg.order.exception.OrderException;
import com.ssg.order.service.OrderService;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping()
    public OrderInfoResponseDTO createOrder(
            @RequestBody List<OrderCreateRequestDTO> orders
    ){
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for(OrderCreateRequestDTO item : orders){
            orderItems.add(
                    OrderItemDTO.builder()
                            .ordQty(item.ordQty())
                            .product(
                                    ProductDTO.builder()
                                            .prdId(item.prdId())
                                            .build()
                            )
                            .build()
            );
        }

        OrderDTO order = orderService.createOrder(orderItems);

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

    @DeleteMapping("/{ordId}/product/{prdId}")
    public OrderCancelResponseDTO cancelOrder(@PathVariable Long ordId, @PathVariable Long prdId){
        OrderDTO order = orderService.cancelOrder(ordId, prdId);

        BigDecimal totalAmount = BigDecimal.ZERO; //취소 후 전체 남은 금액
        BigDecimal refundAmount = null; //환불금액
        ProductDTO resultProduct = null; //취소된 상품

        for(OrderItemDTO item : order.orderItems()){
            //해당 아이템 결제금액
            BigDecimal itemAmount = BigDecimal.valueOf(item.ordQty()).multiply(item.product().stdUprc().subtract(item.product().dcAmt()));
            if("00".equals(item.ordItemSt())) {
                //주문총합 = 기존총합 + (주문수량*(기존단가-할인금액))
                totalAmount = totalAmount.add(itemAmount);
            }
            else if("01".equals(item.ordItemSt()) && prdId.equals(item.product().prdId())){
                refundAmount = itemAmount;
                resultProduct = item.product();
            }
        }
        if(resultProduct == null || refundAmount == null){
            throw new OrderException("조회 비즈니스 에러입니다.");
        }

       return OrderCancelResponseDTO.builder()
               .rfnAmt(refundAmount)
               .totOrdAmt(totalAmount)
               .product(
                       ProductInfoResponseDTO.builder()
                               .dcAmt(resultProduct.dcAmt())
                               .prdId(resultProduct.prdId())
                               .prdNm(resultProduct.prdNm())
                               .stdUprc(resultProduct.stdUprc())
                               .build()
               )
               .build();

    }
}
