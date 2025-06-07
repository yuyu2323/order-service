package com.ssg.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreateRequestDTO(
        @Schema(description = "상품 ID", example = "1000000001")
        Long prdId,
        @Schema(description = "주문수량", example = "5")
        Long ordQty
) {
}
