package com.sparta.cafeorderback.repository;

import com.sparta.cafeorderback.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}