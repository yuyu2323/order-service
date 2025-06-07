package com.ssg.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemInfoResponseDTO(
        @Schema(description = "주문아이템ID", example = "1")
        Long ordItemId,
        @Schema(description = "주문수량", example = "1")
        Long ordQty,
        @Schema(description = "주문아이템상태", example = "00")
        String ordItemSt,
        @Schema(description = "지불금액", example = "3500")
        BigDecimal paidAmt,

        ProductInfoResponseDTO product
) {}
