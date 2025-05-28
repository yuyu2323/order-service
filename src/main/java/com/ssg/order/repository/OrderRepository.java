package com.ssg.order.repository;

import com.ssg.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrdId(Long ordId);
}
