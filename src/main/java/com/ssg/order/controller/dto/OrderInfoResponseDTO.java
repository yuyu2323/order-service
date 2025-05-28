package com.ssg.order.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderInfoResponseDTO(
        Long ordNo,
        BigDecimal sumPrice,
        List<OrderItemInfoResponseDTO> orderItems
) {}
