package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

  private final int statusCode;
  private final String message;

  @Builder
  public ErrorResponse(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public static ErrorResponse of(ErrorCode errorCode) {
    return ErrorResponse.builder()
        .statusCode(errorCode.getHttpStatus().value())
        .message(errorCode.getMessage())
        .build();
  }
}
