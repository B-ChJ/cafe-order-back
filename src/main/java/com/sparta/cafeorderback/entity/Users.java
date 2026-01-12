package com.sparta.cafeorderback.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class Users {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique=true, nullable=false)
	private String email;

	@Column(nullable=false)
	private String password;

	private Long point = 0L;

	private long version;

	private LocalDateTime created_at;

	private LocalDateTime updated_at;
}
