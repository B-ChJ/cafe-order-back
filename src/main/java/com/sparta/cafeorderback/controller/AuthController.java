package com.sparta.cafeorderback.controller;

import com.sparta.cafeorderback.dto.LoginRequest;
import com.sparta.cafeorderback.dto.LoginResponse;
import com.sparta.cafeorderback.dto.RegisterRequest;
import com.sparta.cafeorderback.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
    authService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    LoginResponse response = authService.loginUser(request);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
