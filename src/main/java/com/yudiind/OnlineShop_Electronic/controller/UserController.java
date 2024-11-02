package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.model.dto.*;
import com.yudiind.OnlineShop_Electronic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody ResetPasswordRequestDTO requestDTO){
        userService.resetPasswordRequest(requestDTO.getEmail());
        return ResponseEntity.ok("Password reset code send to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO requestDTO){
        userService.changePassword(requestDTO);
        return ResponseEntity.ok("Password change successfully");
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest request,
                                                      BindingResult bindingResult) throws IOException {


        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError ->
                    errors.put(fieldError.getField(),
                            fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        UserResponseDTO responseDTO = userService.updateUser(request);
        return ResponseEntity.ok().body(responseDTO);
    }
}
