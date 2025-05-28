package com.ssg.order.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderDTO(
    Long ordId,
    List<OrderItemDTO> orderItems
)
{}
