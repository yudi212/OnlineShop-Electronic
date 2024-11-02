package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;

/**
 This DTO will represent the response with the updated cart details.
 */
@Data
public class CartItemResponseDTO {

    private Long productId;
    private String productName;
    private Integer amount;
    private Float price;
    private String imageUrl;
}
