package com.sparta.cafeorderback.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sparta.cafeorderback.dto.OrderCompletedResponse;
import com.sparta.cafeorderback.dto.OrderEvent;
import com.sparta.cafeorderback.dto.OrderItemRequest;
import com.sparta.cafeorderback.dto.OrderRequest;
import com.sparta.cafeorderback.entity.Menu;
import com.sparta.cafeorderback.entity.Order;
import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.repository.MenuRepository;
import com.sparta.cafeorderback.repository.OrderRepository;
import com.sparta.cafeorderback.repository.PointHistoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private UsersService usersService;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PointHistoryRepository pointHistoryRepository;

  @Mock
  private KafkaTemplate<String, OrderEvent> orderMessageKafkaTemplate;

  @InjectMocks
  private OrderService orderService;

  private Users testUser;
  private Menu menu1;
  private Menu menu2;

  @BeforeEach
  void setUp() {
    testUser = new Users("testuser", "testuser@test.com", "password");
    testUser.chargePoint(100000L);

    menu1 = new Menu("아메리카노", "고소한 원두", 4000L, "ON_SALE");
    menu2 = new Menu("카페라떼", "부드러운 우유", 4500L, "ON_SALE");

    ReflectionTestUtils.setField(menu1, "id", 1L);
    ReflectionTestUtils.setField(menu2, "id", 2L);
  }

  @Test
  @DisplayName("주문 생성 성공 테스트")
  void createOrder_Success() {
    // given
    String userEmail = "testuser@test.com";
    OrderItemRequest itemReq1 = new OrderItemRequest(1L, 2);
    OrderItemRequest itemReq2 = new OrderItemRequest(2L, 1);
    OrderRequest orderRequest = new OrderRequest(List.of(itemReq1, itemReq2));
    long expectedTotalPrice = (4000L * 2) + (4500L);

    List<Long> menuIds = List.of(1L, 2L);

    given(usersService.findUserByEmail(userEmail)).willReturn(testUser);
    given(menuRepository.findAllByIdWithPessimisticLock(menuIds)).willReturn(List.of(menu1, menu2));

    // when
    OrderCompletedResponse response = orderService.createOrder(userEmail, orderRequest);

    // then
    assertNotNull(response);
    assertEquals(expectedTotalPrice, response.getTotalPrice());
    assertEquals("RECEIVED", response.getStatus());

    verify(orderRepository, times(1)).save(any(Order.class));
    verify(pointHistoryRepository, times(1)).save(any());
    verify(orderMessageKafkaTemplate, times(1)).send(anyString(), any(OrderEvent.class));

    assertEquals(100000L - expectedTotalPrice, testUser.getPoint());
  }

  @Test
  @DisplayName("주문 생성 실패 테스트 - 포인트 부족")
  void createOrder_Fail_InsufficientPoints() {
    // given
    ReflectionTestUtils.setField(testUser, "point", 1000L);

    String userEmail = "testuser@test.com";
    OrderItemRequest itemReq1 = new OrderItemRequest(1L, 2);
    OrderRequest orderRequest = new OrderRequest(List.of(itemReq1));

    List<Long> menuIds = List.of(1L);

    given(usersService.findUserByEmail(userEmail)).willReturn(testUser);
    given(menuRepository.findAllByIdWithPessimisticLock(menuIds)).willReturn(List.of(menu1));

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      orderService.createOrder(userEmail, orderRequest);
    });

    assertEquals(ErrorCode.INSUFFICIENT_POINTS, exception.getErrorCode());

    verify(orderRepository, times(0)).save(any(Order.class));
    verify(orderMessageKafkaTemplate, times(0)).send(anyString(), any(OrderEvent.class));
  }
}
