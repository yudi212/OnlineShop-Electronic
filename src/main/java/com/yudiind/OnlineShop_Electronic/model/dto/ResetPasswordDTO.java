package com.yudiind.OnlineShop_Electronic.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String resetCode;

    @NotBlank
    @Size(min = 8, max = 55)
    private String newPassword;
}
