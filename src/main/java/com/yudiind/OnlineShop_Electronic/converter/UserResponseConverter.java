package com.yudiind.OnlineShop_Electronic.converter;

import com.yudiind.OnlineShop_Electronic.model.dto.UserResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseConverter {

    public UserResponseDTO converToUserResponseDTO(User user){

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setAddress(user.getAddress());
        userResponse.setCity(user.getCity());
        userResponse.setPhoneNumber(user.getPhoneNumber());

        return userResponse;

    }
}
