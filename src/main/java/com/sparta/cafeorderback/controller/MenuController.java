package com.sparta.cafeorderback.controller;

import com.sparta.cafeorderback.dto.MenuDetailsResponse;
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

  @GetMapping("/coffee")
  public ResponseEntity<List<MenuDetailsResponse>> getAll() {
    List<MenuDetailsResponse> responses = menuService.getAllCoffee();
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }
}
