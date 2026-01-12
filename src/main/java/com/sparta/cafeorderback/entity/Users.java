package com.sparta.cafeorderback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class Users extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  private Long point = 0L;

  @Version
  private Long version;

  public Users(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.point = 0L;
    this.version = 0L;
  }

  public void chargePoint(Long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다.");
    }
    this.point += amount;
  }
}
