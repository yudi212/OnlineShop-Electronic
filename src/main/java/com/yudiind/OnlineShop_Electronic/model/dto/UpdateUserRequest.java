package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;

import  jakarta.validation.constraints.Pattern;
import  jakarta.validation.constraints.Size;
import  jakarta.validation.constraints.NotBlank;
@Data
public class UpdateUserRequest {

    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    @Size(min = 3, max = 26, message = "firsname is required" )
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    @Size(min = 3, max = 26, message = "lastname is required")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    @Size(min = 3, max = 100, message = "address is required" )
    private String address;

    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    @Size(min = 3, max = 100, message = "city is required")
    private String city;

    @Pattern(regexp = "[0-9]+")
    @Size(min = 11, max = 12, message = "size must be between 11 and 12")
    private String phoneNumber;
}
