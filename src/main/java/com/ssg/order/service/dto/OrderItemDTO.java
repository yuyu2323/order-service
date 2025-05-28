package com.ssg.order.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemDTO(
        Long ordItemId,
        String ordItemSt,
        Long ordQty,

        ProductDTO product
)
{}
