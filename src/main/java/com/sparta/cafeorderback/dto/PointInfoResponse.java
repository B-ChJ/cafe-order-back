package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.entity.PointHistory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointInfoResponse {

  private final Long userId;
  private final Long amount;
  private final Long currentPoint;
  private final LocalDateTime chargedAt;

  public static PointInfoResponse from(PointHistory pointHistory) {
    return new PointInfoResponse(
        pointHistory.getUser().getId(),
        pointHistory.getAmount(),
        pointHistory.getUser().getPoint(),
        pointHistory.getChargedAt()
    );
  }
}
