package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {

    private Long id;

    private String name;

    private Float price;

    private Integer amount;
}
