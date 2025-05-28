package com.ssg.order.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemInfoResponseDTO(
        Long ordItemId,
        Long ordQty,
        String ordItemSt,
        BigDecimal paidAmt,

        ProductInfoResponseDTO product
) {}
