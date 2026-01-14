package com.sparta.cafeorderback.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sparta.cafeorderback.dto.MenuDetailsResponse;
import com.sparta.cafeorderback.entity.Menu;
import com.sparta.cafeorderback.repository.MenuRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

  @Mock
  private MenuRepository menuRepository;

  @InjectMocks
  private MenuService menuService;

  @Test
  @DisplayName("전체 메뉴 조회 성공 테스트")
  void getAllCoffee_Success() {
    // given
    Menu menu1 = new Menu("아메리카노", "고소한 원두", 4000L, "ON_SALE");
    Menu menu2 = new Menu("카페라떼", "부드러운 우유", 4500L, "ON_SALE");
    List<Menu> mockMenuList = List.of(menu1, menu2);

    given(menuRepository.findAll()).willReturn(mockMenuList);

    // when
    List<MenuDetailsResponse> responses = menuService.getAllCoffee();

    // then
    assertNotNull(responses);
    assertEquals(2, responses.size());
    assertEquals("아메리카노", responses.get(0).getName());
    assertEquals(4500L, responses.get(1).getPrice());

    verify(menuRepository, times(1)).findAll();
  }
}
