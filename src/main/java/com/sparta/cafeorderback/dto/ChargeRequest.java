package com.sparta.cafeorderback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargeRequest {

  private Long amount;

  public ChargeRequest(Long amount) {
    this.amount = amount;
  }
}
