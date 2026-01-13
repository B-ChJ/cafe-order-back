package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.OrderCompletedResponse;
import com.sparta.cafeorderback.dto.OrderEvent;
import com.sparta.cafeorderback.dto.OrderItemEvent;
import com.sparta.cafeorderback.dto.OrderItemRequest;
import com.sparta.cafeorderback.dto.OrderRequest;
import com.sparta.cafeorderback.entity.Menu;
import com.sparta.cafeorderback.entity.Order;
import com.sparta.cafeorderback.entity.OrderItem;
import com.sparta.cafeorderback.entity.PointHistory;
import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.repository.MenuRepository;
import com.sparta.cafeorderback.repository.OrderRepository;
import com.sparta.cafeorderback.repository.PointHistoryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service

@RequiredArgsConstructor

public class OrderService {


  private static final String ORDER_TOPIC = "order-received";
  private final UsersService usersService;
  private final MenuRepository menuRepository;
  private final OrderRepository orderRepository;
  private final PointHistoryRepository pointHistoryRepository;
  private final KafkaTemplate<String, OrderEvent> orderMessageKafkaTemplate;

  @Transactional
  public OrderCompletedResponse createOrder(String userEmail, OrderRequest orderRequest) {
    Users user = usersService.findUserByEmail(userEmail);

    List<Long> menuIds = orderRequest.getOrderItems().stream()
        .map(OrderItemRequest::getMenuId)
        .collect(Collectors.toList());

    List<OrderItemEvent> orderItemEvents = orderRequest.getOrderItems().stream()
        .map(item -> new OrderItemEvent(item.getMenuId(), item.getQuantity()))
        .collect(Collectors.toList());

    List<Menu> menus = menuRepository.findAllByIdWithPessimisticLock(menuIds);
    if (menus.size() != menuIds.size()) {
      throw new CustomException(ErrorCode.MENU_NOT_FOUND);
    }

    Map<Long, Menu> menuMap = menus.stream()
        .collect(Collectors.toMap(Menu::getId, menu -> menu));

    long totalPrice = 0L;
    Order order = new Order(user, 0L, "PENDING");
    for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
      Menu menu = menuMap.get(itemRequest.getMenuId());

      OrderItem orderItem = new OrderItem(menu, itemRequest.getQuantity());
      order.addOrderItem(orderItem);

      totalPrice += orderItem.getPrice() * itemRequest.getQuantity();

    }

    if (user.getPoint() < totalPrice) {
      throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
    }

    user.reducePoint(totalPrice);

    order.setTotalPrice(totalPrice);

    order.setStatus("RECEIVED");

    orderRepository.save(order);

    PointHistory pointHistory = new PointHistory(user, -totalPrice, "ORDER_PAYMENT");
    pointHistoryRepository.save(pointHistory);
    orderMessageKafkaTemplate.send(ORDER_TOPIC, new OrderEvent(orderItemEvents));
    return OrderCompletedResponse.from(order);
  }
}