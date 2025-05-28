package com.ssg.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "PRODUCT")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_seq")
    @SequenceGenerator(name = "prd_seq", sequenceName = "prd_seq", allocationSize = 1)
    private Long prdId;

    private String prdNm;

    private BigDecimal stdUprc;
    private BigDecimal dcAmt;

    private Long stkQty;

}
