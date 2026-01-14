package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.ChargeRequest;
import com.sparta.cafeorderback.dto.PointInfoResponse;
import com.sparta.cafeorderback.entity.PointHistory;
import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

  private final UsersService usersService;

  private final PointHistoryRepository pointHistoryRepository;

  @Transactional
  public PointInfoResponse chargeMyPoint(String userEmail, ChargeRequest request) {
    Users user = usersService.findUserByEmail(userEmail);

    try {
      user.chargePoint(request.getAmount());
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage());
      throw new CustomException(ErrorCode.INVALID_ARGUMENT);
    }
    PointHistory pointHistory = new PointHistory(user, request.getAmount(), "CHARGE");
    pointHistoryRepository.save(pointHistory);

    return PointInfoResponse.from(pointHistory);
  }
}
