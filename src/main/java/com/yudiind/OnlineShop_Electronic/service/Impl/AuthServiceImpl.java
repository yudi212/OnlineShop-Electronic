package com.yudiind.OnlineShop_Electronic.service.Impl;

import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.LoginUserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.UserRepository;
import com.yudiind.OnlineShop_Electronic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User login(LoginUserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail());
        if (Objects.isNull(user)){
            throw new ResourceNotFoundException("User not found");
        }

        if (!user.isActive()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not activated.");
        }

        if (passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong");
        }

    }

//    @Override
//    public void logout(User user) {
//
//    }
}
