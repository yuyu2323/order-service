package com.ssg.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCancelResponseDTO(
        @Schema(description = "환불금액", example = "37000")
        BigDecimal rfnAmt,
        @Schema(description = "주문금액총합", example = "3500")
        BigDecimal totOrdAmt,

        ProductInfoResponseDTO product
) {}
