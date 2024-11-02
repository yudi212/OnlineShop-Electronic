package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "email address cannot be empty")
    private String email;

    @NotBlank(message = "First Name cannot be empty")
    private String firstName;

    @Size(min = 8, max = 55, message = "Password Character Length")
    private String password;

    @Size(min = 8, max = 55, message = "Password2 Character Length")
    private String password2;

}
