package com.ssg.order.repository;

import com.ssg.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByPrdIdIn(List<Long> prdIds);
    Optional<Product> findByPrdId(Long prdId);
}
