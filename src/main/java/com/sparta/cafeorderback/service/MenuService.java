package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.MenuDetailsResponse;
import com.sparta.cafeorderback.repository.MenuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menuRepository;

  @Transactional(readOnly = true)
  public List<MenuDetailsResponse> getAllCoffee() {

    return menuRepository.findAll().stream()
        .map(coffee -> MenuDetailsResponse.from(coffee))
        .toList();
  }
}
