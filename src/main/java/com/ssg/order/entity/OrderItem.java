package com.ssg.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "order_item")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ordItemId; //아이템ID

    private Long ordNo; //주문번호
    private String ordItemSt; //주문상태 주문:00 취소:01

    private String prdNm; //상품명
    private BigDecimal stdUprc; //표준가격
    private BigDecimal dcAmt; //할인금액
    private Long ordQty; //주문수량

}
