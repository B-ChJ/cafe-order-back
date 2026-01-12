package com.sparta.cafeorderback.controller;

import com.sparta.cafeorderback.dto.ChargeRequest;
import com.sparta.cafeorderback.dto.PointInfoResponse;
import com.sparta.cafeorderback.security.CustomUserDetails;
import com.sparta.cafeorderback.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointController {

  private final PointService pointService;

  @PostMapping("/me/points/charge")
  public ResponseEntity<PointInfoResponse> charge(@AuthenticationPrincipal CustomUserDetails user,
      @RequestBody @Valid ChargeRequest request) {
    PointInfoResponse response = pointService.chargeMyPoint(user.getUsername(), request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
