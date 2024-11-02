package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;

import java.util.List;

/**
 This DTO will represent the response with the updated cart details.
 */
@Data
public class CartResponseDTO {

    private Long cartId;
    private List<CartItemResponseDTO> items;
    private Float totalPrice;
}
