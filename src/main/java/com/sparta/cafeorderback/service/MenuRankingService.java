package com.sparta.cafeorderback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.cafeorderback.dto.OrderItemEvent;
import com.sparta.cafeorderback.dto.Top3MenusResponse;
import com.sparta.cafeorderback.repository.MenuRepository;
import com.sparta.cafeorderback.scheduler.MenuRankingScheduler;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuRankingService {

  private static final String RANKING_KEY_PREFIX = "menu:ranking:";
  private static final long KEY_EXPIRATION_DAYS = 4;
  private final StringRedisTemplate stringRedisTemplate;
  private final MenuRepository menuRepository;
  private final ObjectMapper objectMapper;

  public void increaseRankingScore(List<OrderItemEvent> orderItems) {
    String todayKey = RANKING_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    for (OrderItemEvent item : orderItems) {
      stringRedisTemplate.opsForZSet()
          .incrementScore(todayKey, item.getMenuId().toString(), item.getQuantity());
    }
    stringRedisTemplate.expire(todayKey, KEY_EXPIRATION_DAYS, TimeUnit.DAYS);
  }

  @Transactional(readOnly = true)
  public List<Top3MenusResponse> getTop3Menus() {
    String cachedRanking = stringRedisTemplate.opsForValue()
        .get(MenuRankingScheduler.TOP3_CACHE_KEY);

    if (cachedRanking == null || cachedRanking.isEmpty()) {
      log.info("캐시된 TOP3 랭킹 데이터가 없습니다.");
      return Collections.emptyList();
    }

    try {
      return objectMapper.readValue(cachedRanking, new TypeReference<List<Top3MenusResponse>>() {
      });
    } catch (JsonProcessingException e) {
      log.error("TOP3 랭킹 데이터 JSON 역직렬화 실패", e);
      return Collections.emptyList();
    }
  }
}
