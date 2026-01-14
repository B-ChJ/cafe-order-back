package com.sparta.cafeorderback.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {

    @NotEmpty
    @Valid
    private List<OrderItemRequest> orderItems;
}
