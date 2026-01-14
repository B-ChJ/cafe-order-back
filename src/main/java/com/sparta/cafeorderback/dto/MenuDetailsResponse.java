package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuDetailsResponse {

  private final Long id;
  private final String name;
  private final Long price;
  private final String description;

  public static MenuDetailsResponse from(Menu menu) {
    return new MenuDetailsResponse(
        menu.getId(),
        menu.getName(),
        menu.getPrice(),
        menu.getDescription()
    );
  }
}
