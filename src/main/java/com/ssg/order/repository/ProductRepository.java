package com.ssg.order.repository;

import com.ssg.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByPrdIdIn(List<Long> prdIds);
}
