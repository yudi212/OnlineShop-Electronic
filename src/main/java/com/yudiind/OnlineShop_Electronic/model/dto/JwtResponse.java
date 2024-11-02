package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JwtResponse {

    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
