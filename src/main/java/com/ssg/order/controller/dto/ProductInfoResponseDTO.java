package com.ssg.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponseDTO(
        @Schema(description = "상품ID", example = "1000000002")
        Long prdId,
        @Schema(description = "상품명", example = "신라면 멀티팩")
        String prdNm,
        @Schema(description = "표준단가", example = "4200")
        BigDecimal stdUprc,
        @Schema(description = "할인금액", example = "500")
        BigDecimal dcAmt
) {}
