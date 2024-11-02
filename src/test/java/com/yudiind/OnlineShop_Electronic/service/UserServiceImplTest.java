package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.converter.UserResponseConverter;
import com.yudiind.OnlineShop_Electronic.error.exception.ConstrainViolationException;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.ChangePasswordRequestDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ResetPasswordDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.UpdateUserRequest;
import com.yudiind.OnlineShop_Electronic.model.dto.UserResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.UserRepository;
import com.yudiind.OnlineShop_Electronic.security.UserPrincipal;
import com.yudiind.OnlineShop_Electronic.service.Impl.MailService;
import com.yudiind.OnlineShop_Electronic.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserResponseConverter userResponseConverter;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodePassword");
        user.setPasswordResetCode("resetCode321");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setCity("New York");
        user.setPhoneNumber("12345678901");

        when(userRepository.findByEmail(anyString())).thenReturn(user);
    }

    @Test
    void reset_password_request_success() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        userService.resetPasswordRequest("test@example.com");

        verify(userRepository, times(1)).save(user);
        verify(mailService, times(1)).sendResetPasswordEmail("test@example.com", user.getPasswordResetCode());
    }

    @Test
    void reset_password_request_not_found() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        assertThatThrownBy(()-> userService.resetPasswordRequest("test@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void reset_password_success() {

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setEmail("test@example.com");
        resetPasswordDTO.setResetCode("resetCode321");
        resetPasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(resetPasswordDTO.getEmail())).thenReturn(user);
        when(passwordEncoder.encode(resetPasswordDTO.getNewPassword())).thenReturn("encodeNewPassword");

        userService.resetPassword(resetPasswordDTO);

        verify(userRepository, times(1)).save(user);

        assertNull(user.getPasswordResetCode());
        assertEquals("encodeNewPassword", user.getPassword());
    }

    @Test
    void reset_password_invalid_reset_code() {

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setEmail("test@example.com");
        resetPasswordDTO.setResetCode("resetCode");
        resetPasswordDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(resetPasswordDTO.getEmail())).thenReturn(user);

        assertThatThrownBy(()-> userService.resetPassword(resetPasswordDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid reset code");
    }



    @Test
    void change_password_success() {

        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO();
        changePasswordRequestDTO.setEmail("test@example.com");
        changePasswordRequestDTO.setOldPassword("oldPassword");
        changePasswordRequestDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(changePasswordRequestDTO.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("oldPassword","encodePassword" )).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodeNewPassword");

        userService.changePassword(changePasswordRequestDTO);

        verify(userRepository, times(1)).save(user);

        assertEquals("encodeNewPassword", user.getPassword());
    }

    @Test
    void change_password_invalid_oldPassword() {

        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO();
        changePasswordRequestDTO.setEmail("test@example.com");
        changePasswordRequestDTO.setOldPassword("oldPassword");
        changePasswordRequestDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(changePasswordRequestDTO.getEmail())).thenReturn(user);

        assertThatThrownBy(()-> userService.changePassword(changePasswordRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Old password is incorrect");
    }

    @Test
    void authenticate_user_success() {

        UserPrincipal principal = new UserPrincipal(user.getId(),"encodePassword", "test@example.com", true, user.getRoles());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        User authenticatedUser = userService.getAuthenticateUser();

        // Then
        assertNotNull(authenticatedUser);
        assertEquals("test@example.com", authenticatedUser.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testGetAuthenticateUser_UserNotFound() {

        UserPrincipal principal = new UserPrincipal(user.getId(),"encodePassword", "test@example.com", true, user.getRoles());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getAuthenticateUser();
        });
    }


    @Test
    void update_user_success() {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setLastName("lasu");
        request.setAddress("jl. antah barantah");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userResponseConverter.converToUserResponseDTO(user)).thenReturn(new UserResponseDTO());

        UserPrincipal principal = new UserPrincipal(user.getId(),"encodePassword", "test@example.com", true, user.getRoles());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserResponseDTO response = userService.updateUser(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(user);
        assertEquals("lasu", user.getLastName());
        assertEquals("jl. antah barantah", user.getAddress());

    }
}
