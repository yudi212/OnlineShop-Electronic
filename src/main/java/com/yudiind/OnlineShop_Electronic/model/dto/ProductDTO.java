package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    private Long categoryId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "sku is required")
    private String sku;

    @NotNull(message = "price is required")
    private Float price;

    @NotNull(message = "stock is required")
    private Integer stock;

    @NotBlank(message = "Description is required")
    private String longDesc;

}
