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
    private Long prdId;

    private String prdNm;

    private BigDecimal stdUprc;
    private BigDecimal dcAmt;

    private Long stkQty;

}
