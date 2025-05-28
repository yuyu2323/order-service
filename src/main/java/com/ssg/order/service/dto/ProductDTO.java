package com.ssg.order.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDTO(
        Long prdId,
        String prdNm,
        BigDecimal stdUprc,
        BigDecimal dcAmt
) {}
