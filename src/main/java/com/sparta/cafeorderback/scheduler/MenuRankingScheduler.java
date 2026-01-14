package com.sparta.cafeorderback.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.cafeorderback.dto.Top3MenusResponse;
import com.sparta.cafeorderback.entity.Menu;
import com.sparta.cafeorderback.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuRankingScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final MenuRepository menuRepository;
    private final ObjectMapper objectMapper;

    private static final String RANKING_KEY_PREFIX = "menu:ranking:";
    public static final String TOP3_CACHE_KEY = "ranking:top3:cache";

    // 매 3분마다 실행 (180000 밀리초)
    @Scheduled(fixedRate = 180000)
    public void updateTop3MenuRanking() {
        log.info("인기 메뉴 TOP3 랭킹 집계 스케줄 시작");

        // 1. 최근 3일간의 랭킹 데이터를 합산
        List<String> dateKeys = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            dateKeys.add(RANKING_KEY_PREFIX + today.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        String unionKey = "ranking:union:temp";
        if (dateKeys.size() > 1) {
            stringRedisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), dateKeys.subList(1, dateKeys.size()), unionKey);
        } else if (!dateKeys.isEmpty()) {
            stringRedisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), Collections.emptyList(), unionKey);
        } else {
            log.info("집계할 랭킹 데이터가 없습니다.");
            // 캐시된 데이터가 있다면 삭제 (데이터가 없을 때도 이전 캐시가 남아있지 않도록)
            stringRedisTemplate.delete(TOP3_CACHE_KEY);
            return;
        }

        // 2. 합산된 랭킹에서 TOP 3 추출
        Set<TypedTuple<String>> topMenusTuple = stringRedisTemplate.opsForZSet().reverseRangeWithScores(unionKey, 0, 2);

        // 임시 키 삭제
        stringRedisTemplate.delete(unionKey);

        if (topMenusTuple == null || topMenusTuple.isEmpty()) {
            log.info("TOP3 메뉴가 없습니다.");
            // 캐시된 데이터가 있다면 삭제 (데이터가 없을 때도 이전 캐시가 남아있지 않도록)
            stringRedisTemplate.delete(TOP3_CACHE_KEY);
            return;
        }

        // 3. DB에서 메뉴 상세 정보 조회
        List<Long> topMenuIds = topMenusTuple.stream()
                .map(TypedTuple::getValue)
                .map(Long::parseLong)
                .toList();

        Map<Long, Menu> topMenuMap = menuRepository.findAllById(topMenuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        // 4. 최종 응답 형태(List<Top3MenusResponse>)로 가공
        List<Top3MenusResponse> top3MenusResponses = topMenusTuple.stream()
                .map(tuple -> {
                    Long menuId = Long.parseLong(tuple.getValue());
                    Menu menu = topMenuMap.get(menuId);
                    Long orderCount = Optional.ofNullable(tuple.getScore()).map(Double::longValue).orElse(0L);
                    return new Top3MenusResponse(menu, orderCount);
                })
                .toList();

        // 5. Redis에 최종 결과를 JSON 문자열로 캐시
        try {
            String cachedData = objectMapper.writeValueAsString(top3MenusResponses);
            // 캐시 유효 시간을 스케줄 주기(3분)보다 길게 설정 (예: 5분)하여 안정성 확보
            stringRedisTemplate.opsForValue().set(TOP3_CACHE_KEY, cachedData, 5, TimeUnit.MINUTES);
            log.info("인기 메뉴 TOP3 랭킹 캐시 업데이트 완료");
        } catch (JsonProcessingException e) {
            log.error("TOP3 랭킹 데이터 JSON 직렬화 실패", e);
        }
    }
}
