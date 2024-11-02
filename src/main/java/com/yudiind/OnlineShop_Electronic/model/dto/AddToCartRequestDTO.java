package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;

/**
 This DTO will represent the request body to add a product to the cart.
 */
@Data
public class AddToCartRequestDTO {

    private Long productId;
    private Integer amount;
}
