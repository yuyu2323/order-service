package com.ssg.order.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCancelResponseDTO(
        BigDecimal rfnAmt,
        BigDecimal totOrdAmt,

        ProductInfoResponseDTO product
) {}
