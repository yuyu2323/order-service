package com.ssg.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderInfoResponseDTO(
        @Schema(description = "주문번호", example = "1")
        Long ordId,
        @Schema(description = "주문금액총합", example = "40500")
        BigDecimal totOrdAmt,
        List<OrderItemInfoResponseDTO> orderItems
) {}
