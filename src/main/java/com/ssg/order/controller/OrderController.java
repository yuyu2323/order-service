package com.ssg.order.controller;

import com.ssg.order.common.enums.OrderStatus;
import com.ssg.order.controller.dto.*;
import com.ssg.order.exception.OrderException;
import com.ssg.order.service.OrderService;
import com.ssg.order.service.dto.OrderDTO;
import com.ssg.order.service.dto.OrderItemDTO;
import com.ssg.order.service.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/order")
@Validated
@Tag(name = "Order API", description = "주문 관련 API입니다.")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 주문조회 기능*/
    @GetMapping("/{ordId}")
    @Operation(summary = "주문 조회", description = "주문 번호로 주문 상세 정보를 조회합니다.")
    public OrderInfoResponseDTO searchOrder(
            @Parameter(description = "주문번호", example = "1")
            @PathVariable @Min(1) Long ordId){

        OrderDTO order = orderService.findByOrdId(ordId);

        List<OrderItemInfoResponseDTO> resultItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemDTO item : order.orderItems()){

            BigDecimal paidAmt = BigDecimal.valueOf(item.ordQty()).multiply(item.product().stdUprc().subtract(item.product().dcAmt()));

            if(OrderStatus.CREATED.getCode().equals(item.ordItemSt())) {
                //주문총합 = 기존총합 + (주문수량*(기존단가-할인금액))
                totalAmount = totalAmount.add(paidAmt);
            }

            resultItems.add(
                    OrderItemInfoResponseDTO.builder()
                            .ordItemId(item.ordItemId())
                            .ordQty(item.ordQty())
                            .ordItemSt(item.ordItemSt())
                            .paidAmt(paidAmt)
                            .product(ProductInfoResponseDTO.builder()
                                    .prdId(item.product().prdId())
                                    .prdNm(item.product().prdNm())
                                    .dcAmt(item.product().dcAmt())
                                    .stdUprc(item.product().stdUprc())
                                    .build())
                            .build()
            );
        }

        return OrderInfoResponseDTO.builder()
                .orderItems(resultItems)
                .totOrdAmt(totalAmount)
                .ordId(order.ordId())
                .build();
    }

    /** 주문생성 기능*/
    @PostMapping()
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public OrderInfoResponseDTO createOrder(
            @RequestBody @NotEmpty(message = "주문 항목은 비어 있을 수 없습니다.") List<@Valid OrderCreateRequestDTO> orders
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

        List<OrderItemInfoResponseDTO> resultItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemDTO item : order.orderItems()){

            BigDecimal paidAmt = BigDecimal.valueOf(item.ordQty()).multiply(item.product().stdUprc().subtract(item.product().dcAmt()));

            if(OrderStatus.CREATED.getCode().equals(item.ordItemSt())) {
                //주문총합 = 기존총합 + (주문수량*(기존단가-할인금액))
                totalAmount = totalAmount.add(paidAmt);
            }

            resultItems.add(
                    OrderItemInfoResponseDTO.builder()
                            .ordItemId(item.ordItemId())
                            .ordQty(item.ordQty())
                            .ordItemSt(item.ordItemSt())
                            .paidAmt(paidAmt)
                            .product(ProductInfoResponseDTO.builder()
                                    .prdId(item.product().prdId())
                                    .prdNm(item.product().prdNm())
                                    .dcAmt(item.product().dcAmt())
                                    .stdUprc(item.product().stdUprc())
                                    .build())
                            .build()
            );
        }

        return OrderInfoResponseDTO.builder()
                .orderItems(resultItems)
                .totOrdAmt(totalAmount)
                .ordId(order.ordId())
                .build();
    }

    /** 주문취소 기능*/
    @PutMapping("/{ordId}/product/{prdId}/cancel")
    @Operation(summary = "주문 취소", description = "생성된 주문을 취소합니다.")
    public OrderCancelResponseDTO cancelOrder(
            @Parameter(description = "주문번호", example = "1")
            @PathVariable @Min(1) Long ordId,
            @Parameter(description = "상품 ID", example = "1000000002")
            @PathVariable @Min(1) Long prdId){
        OrderDTO order = orderService.cancelOrder(ordId, prdId);

        BigDecimal totalAmount = BigDecimal.ZERO; //취소 후 전체 남은 금액
        BigDecimal refundAmount = null; //환불금액
        ProductDTO resultProduct = null; //취소된 상품

        for(OrderItemDTO item : order.orderItems()){
            //해당 아이템 결제금액
            BigDecimal itemAmount = BigDecimal.valueOf(item.ordQty()).multiply(item.product().stdUprc().subtract(item.product().dcAmt()));
            if(OrderStatus.CREATED.getCode().equals(item.ordItemSt())) {
                //주문총합 = 기존총합 + (주문수량*(기존단가-할인금액))
                totalAmount = totalAmount.add(itemAmount);
            }
            else if(OrderStatus.CANCELED.getCode().equals(item.ordItemSt()) && prdId.equals(item.product().prdId())){
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
