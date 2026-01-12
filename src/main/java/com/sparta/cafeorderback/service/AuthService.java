package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.LoginRequest;
import com.sparta.cafeorderback.dto.LoginResponse;
import com.sparta.cafeorderback.dto.RegisterRequest;
import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.jwt.JwtUtil;
import com.sparta.cafeorderback.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Transactional
  public void registerUser(RegisterRequest request) {
    if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
    }

    Users user = new Users(request.getName(),
        request.getEmail(),
        passwordEncoder.encode(request.getPassword()));

    usersRepository.save(user);
  }

  @Transactional(readOnly = true)
  public LoginResponse loginUser(LoginRequest request) {
    Users user = usersRepository.findByEmail(request.getEmail()).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    String token = jwtUtil.createToken(user.getEmail());

    return LoginResponse.from(user, token);
  }
}
