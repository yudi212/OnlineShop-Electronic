package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.model.dto.UserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RegistrationController {

    private RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrations(@RequestBody @Valid UserRequest userRequest,
                                           BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError ->
                    errors.put(fieldError.getField(), fieldError.toString()));
            return ResponseEntity.badRequest().body(errors);

        }

        User user = registrationService.registration(userRequest);
        return ResponseEntity.ok("User registered successfully. Please check your email to activate your account.");
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activationUser(@RequestParam("code") String code){
        try {
            registrationService.activateUser(code);
            return ResponseEntity.ok().body("User activated successfully.");
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
