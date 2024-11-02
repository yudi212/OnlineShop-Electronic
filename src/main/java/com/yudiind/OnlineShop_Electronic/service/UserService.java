package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.converter.UserResponseConverter;
import com.yudiind.OnlineShop_Electronic.model.dto.ChangePasswordRequestDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ResetPasswordDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.UpdateUserRequest;
import com.yudiind.OnlineShop_Electronic.model.dto.UserResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import com.yudiind.OnlineShop_Electronic.model.entity.User;

public interface UserService {

    User getAuthenticateUser();

    User saveUser(User user);

    UserResponseDTO updateUser(UpdateUserRequest updateUserRequest);

    void resetPasswordRequest(String email);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);
    void changeUserRole(Long userId, Role newRole);

}
