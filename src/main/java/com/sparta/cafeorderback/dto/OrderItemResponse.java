package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemResponse {

  private String menuName;
  private Long price;
  private int quantity;

  public OrderItemResponse(String name, Long price, int quantity) {
    this.menuName = name;
    this.price = price;
    this.quantity = quantity;
  }

  public static OrderItemResponse from(OrderItem orderItem) {
    return new OrderItemResponse(
        orderItem.getMenu().getName(),
        orderItem.getPrice(),
        orderItem.getQuantity()
    );
  }
}
