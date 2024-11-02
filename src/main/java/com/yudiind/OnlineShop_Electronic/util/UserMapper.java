package com.yudiind.OnlineShop_Electronic.util;

import com.yudiind.OnlineShop_Electronic.model.dto.UserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isPasswordValid(String password){
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,55}$";
        return password.matches(passwordPattern);
    }

    public User mapUserRequestToUser(UserRequest userRequest){

        if (userRequest.getEmail() == null || !userRequest.getEmail().matches("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")){
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty()){
            throw new IllegalArgumentException("First Name cannot be empty");
        }

        if (!isPasswordValid(userRequest.getPassword())){
            throw new IllegalArgumentException("Password harus memiliki minimal 8 karakter, mengandung huruf besar, huruf kecil, angka, dan simbol, dan tidak boleh mengandung spasi");
        }

        if (userRequest.getPassword2() == null || !userRequest.getPassword().equals(userRequest.getPassword2())){
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        return user;
    }
}
