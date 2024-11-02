package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import com.yudiind.OnlineShop_Electronic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService  userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/change-role/{userId}")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId,
                                                 @RequestParam Role newRole){
        userService.changeUserRole(userId, newRole);
        return new ResponseEntity<>("User role updated successfully",HttpStatus.OK);
    }

}
