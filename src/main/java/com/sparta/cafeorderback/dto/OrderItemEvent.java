package com.sparta.cafeorderback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private Long menuId;
    private int quantity;
}
