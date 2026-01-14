package com.sparta.cafeorderback.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.cafeorderback.entity.Menu;
import lombok.Getter;

@Getter
public class Top3MenusResponse {

  private final Long menuId;
  private final String menuName;
  private final Long orderCount;

  public Top3MenusResponse(Menu menu, Long orderCount) {
    this.menuId = menu.getId();
    this.menuName = menu.getName();
    this.orderCount = orderCount;
  }

  @JsonCreator
  public Top3MenusResponse(@JsonProperty("menuId") Long menuId,
      @JsonProperty("menuName") String menuName,
      @JsonProperty("orderCount") Long orderCount) {
    this.menuId = menuId;
    this.menuName = menuName;
    this.orderCount = orderCount;
  }
}
