package com.ssg.order.controller.dto;

import lombok.Builder;

@Builder
public record OrderItemInfoResponseDTO(
        Long ordItemId,
        Long ordQty,
        String ordItemSt,
        ProductInfoResponseDTO product
) {}
