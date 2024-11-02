package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.UserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface RegistrationService {

    User registration(UserRequest userRequest) throws ResponseStatusException;

    Optional<User> activateEmailCode(String code);

    void activateUser(String activationCode);
}
