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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ord_item_seq")
    @SequenceGenerator(name = "ord_item_seq", sequenceName = "ord_item_seq", allocationSize = 1)
    private Long ordItemId; //아이템ID

    private String ordItemSt; //주문상태 주문:00 취소:01

    private Long prdId;
    private String prdNm; //상품명
    private BigDecimal stdUprc; //표준가격
    private BigDecimal dcAmt; //할인금액
    private Long ordQty; //주문수량

    @ManyToOne
    @JoinColumn(name = "ord_no")  // FK 컬럼 이름
    private Order order;
}
