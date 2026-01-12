package com.sparta.cafeorderback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargeRequest {

  @NotNull
  @Positive
  private Long amount;

  public ChargeRequest(Long amount) {
    this.amount = amount;
  }
}
