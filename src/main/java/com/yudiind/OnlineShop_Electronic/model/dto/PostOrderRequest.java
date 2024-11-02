package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostOrderRequest {

    @NotBlank
    @Size(min = 3, max = 60)
    private String shipName;

    @NotBlank
    @Size(min = 3, max = 250)
    private String shipAddress;

    @NotBlank
    @Size(min = 3, max = 100)
    private String city;

    @NotBlank
    @Size(min = 11, max = 13)
    @Pattern(regexp = "[0-9]+")
    private String  phone;
}
