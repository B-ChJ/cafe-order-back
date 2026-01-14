package com.sparta.cafeorderback.listener;

import com.sparta.cafeorderback.dto.OrderEvent;
import com.sparta.cafeorderback.service.MenuRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

  private final MenuRankingService menuRankingService;

  @KafkaListener(
      topics = "order-received",
      groupId = "ranking-group",
      containerFactory = "orderMessageKafkaListenerContainerFactory")
  public void consumeOrder(OrderEvent message) {
    log.info("주문 이벤트 수신 (주문 항목: {}개). 랭킹 점수 업데이트를 시작합니다.", message.getOrderItems().size());
    menuRankingService.increaseRankingScore(message.getOrderItems());
  }
}
