package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCompletedResponse {

  private Long orderId;
  private Long userId;
  private Long totalPrice;
  private String status;
  private LocalDateTime orderedAt;
  private List<OrderItemResponse> orderItems;

  public OrderCompletedResponse(Long orderId, Long userId, Long totalPrice, String status,
      LocalDateTime orderedAt, List<OrderItemResponse> orderItems) {
    this.orderId = orderId;
    this.userId = userId;
    this.totalPrice = totalPrice;
    this.status = status;
    this.orderedAt = orderedAt;
    this.orderItems = orderItems;
  }

  public static OrderCompletedResponse from(Order order) {
    List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
        .map(OrderItemResponse::from)
        .toList();

    return new OrderCompletedResponse(
        order.getId(),
        order.getUser().getId(),
        order.getTotalPrice(),
        order.getStatus(),
        order.getCreatedAt(),
        itemResponses
    );
  }
}