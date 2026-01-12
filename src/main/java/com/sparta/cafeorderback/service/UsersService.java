package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;

  public Users findUserByEmail(String email) {

    return usersRepository.findByEmail(email).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );
  }
}
