package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResetPasswordRequestDTO {

    private String email;
}
