package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDTO {

    private String name;
    private Float price;
    private Integer amount;
    private CategoryDTO category;
    private String image;
}
