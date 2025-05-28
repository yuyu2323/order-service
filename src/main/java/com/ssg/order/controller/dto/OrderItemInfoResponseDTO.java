package com.ssg.order.controller.dto;

import lombok.Builder;

@Builder
public record OrderItemInfoResponseDTO(
        Long ordItemNo,
        Long ordQty,

        ProductInfoResponseDTO product
) {}
