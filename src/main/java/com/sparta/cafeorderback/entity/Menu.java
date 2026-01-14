package com.sparta.cafeorderback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menus")
public class Menu extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Lob
  private String description;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private String status;

  public Menu(String name, String description, Long price, String status) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.status = status;
  }
}
