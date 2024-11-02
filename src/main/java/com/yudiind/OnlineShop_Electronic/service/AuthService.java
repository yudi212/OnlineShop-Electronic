package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.LoginUserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.User;

public interface AuthService {

    User login(LoginUserRequest userRequest);

}
