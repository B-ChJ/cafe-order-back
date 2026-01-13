package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.OrderItemEvent;
import com.sparta.cafeorderback.dto.Top3MenusResponse;
import com.sparta.cafeorderback.entity.Menu;
import com.sparta.cafeorderback.repository.MenuRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuRankingService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MenuRepository menuRepository;

    private static final String RANKING_KEY_PREFIX = "menu:ranking:";
    private static final long KEY_EXPIRATION_DAYS = 4;

    public void increaseRankingScore(List<OrderItemEvent> orderItems) {
        String todayKey = RANKING_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        for (OrderItemEvent item : orderItems) {
            stringRedisTemplate.opsForZSet().incrementScore(todayKey, item.getMenuId().toString(), item.getQuantity());
        }
        stringRedisTemplate.expire(todayKey, KEY_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    @Transactional(readOnly = true)
    public List<Top3MenusResponse> getTop3Menus() {
        List<String> dateKeys = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            dateKeys.add(RANKING_KEY_PREFIX + today.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        String unionKey = "ranking:union:last-3-days";
        if (dateKeys.size() > 1) {
            stringRedisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), dateKeys.subList(1, dateKeys.size()), unionKey);
        } else if (!dateKeys.isEmpty()) {
            stringRedisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), Collections.emptyList(), unionKey);
        } else {
            return Collections.emptyList();
        }

        Set<TypedTuple<String>> topMenusTuple = stringRedisTemplate.opsForZSet().reverseRangeWithScores(unionKey, 0, 2);

        stringRedisTemplate.delete(unionKey);

        if (topMenusTuple == null || topMenusTuple.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> topMenuIds = topMenusTuple.stream()
                .map(TypedTuple::getValue)
                .map(Long::parseLong)
                .toList();

        Map<Long, Menu> topMenuMap = menuRepository.findAllById(topMenuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        return topMenusTuple.stream()
                .map(tuple -> {
                    Long menuId = Long.parseLong(tuple.getValue());
                    Menu menu = topMenuMap.get(menuId);
                    Long orderCount = Optional.ofNullable(tuple.getScore()).map(Double::longValue).orElse(0L);
                    return new Top3MenusResponse(menu, orderCount);
                })
                .toList();
    }
}
