package com.ssg.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ordNo; //주문번호

    @OneToMany
    @JoinColumn(name = "ord_no")  // FK 컬럼 이름
    private List<OrderItem> orderItems;
}
