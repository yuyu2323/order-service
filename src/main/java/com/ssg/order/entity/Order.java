package com.ssg.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order_m")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ord_seq")
    @SequenceGenerator(name = "ord_seq", sequenceName = "ord_seq", allocationSize = 1)
    private Long ordId; //주문Id

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ord_no")  // FK 컬럼 이름
    private List<OrderItem> orderItems;

    public void addOrderItem(OrderItem item){
        orderItems.add(item);
        item.setOrder(this);
    }
}
