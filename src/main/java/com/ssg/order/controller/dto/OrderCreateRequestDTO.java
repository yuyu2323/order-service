package com.ssg.order.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreateRequestDTO(
        Long prdId,
        Long ordQty
) {
}
