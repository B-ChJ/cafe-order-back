package com.sparta.cafeorderback.controller;

import com.sparta.cafeorderback.dto.MenuDetailsResponse;
import com.sparta.cafeorderback.dto.Top3MenusResponse;
import com.sparta.cafeorderback.service.MenuRankingService;
import com.sparta.cafeorderback.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

  private final MenuService menuService;
  private final MenuRankingService menuRankingService;

  @GetMapping("/coffee")
  public ResponseEntity<List<MenuDetailsResponse>> getAll() {
    List<MenuDetailsResponse> responses = menuService.getAllCoffee();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @GetMapping("/top3")
  public ResponseEntity<List<Top3MenusResponse>> getMenusTop3() {
    List<Top3MenusResponse> responses = menuRankingService.getTop3Menus();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
