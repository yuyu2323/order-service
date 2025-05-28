package com.ssg.order.controller.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponseDTO(
        Long prdId,
        String prdNm,
        BigDecimal stdUprc,
        BigDecimal dcAmt,
        Long stkQty
) {}
