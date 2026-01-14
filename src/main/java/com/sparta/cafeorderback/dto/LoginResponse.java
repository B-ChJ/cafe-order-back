package com.sparta.cafeorderback.dto;

import com.sparta.cafeorderback.entity.Users;
import lombok.Getter;

@Getter
public class LoginResponse {

  private final Long id;
  private final String name;
  private final String email;
  private final String token;

  private LoginResponse(Long id, String name, String email, String token) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.token = token;
  }

  public static LoginResponse from(Users user, String token) {
    return new LoginResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        token
    );
  }
}
