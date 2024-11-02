package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmCartRequest {

    @NotNull
    private List<CartItemDTO> cartItems;

    @Min(0)
    private Float totalPrice;
}
