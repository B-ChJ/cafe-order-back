package com.sparta.cafeorderback.repository;

import com.sparta.cafeorderback.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
