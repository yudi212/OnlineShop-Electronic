package com.yudiind.OnlineShop_Electronic.service.Impl;

import com.yudiind.OnlineShop_Electronic.converter.UserResponseConverter;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.ChangePasswordRequestDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ResetPasswordDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.UpdateUserRequest;
import com.yudiind.OnlineShop_Electronic.model.dto.UserResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.UserRepository;
import com.yudiind.OnlineShop_Electronic.security.UserPrincipal;
import com.yudiind.OnlineShop_Electronic.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private UserResponseConverter userResponseConverter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, UserResponseConverter userResponseConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.userResponseConverter = userResponseConverter;
    }

    @Override
    public User getAuthenticateUser() {
        // Dapatkan Authentication dari SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Cek apakah principal adalah instance dari UserPrincipal
        if (authentication == null || !(authentication.getPrincipal() instanceof  UserPrincipal)){
            throw new AccessDeniedException("Invalid access");
        }

        // Lakukan casting ke UserPrincipal
        UserPrincipal userPrincipal = (UserPrincipal)  authentication.getPrincipal();

        // Dapatkan user berdasarkan email dari UserPrincipal
        User user = userRepository.findByEmail(userPrincipal.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User saveUser(User user){
        if (Objects.isNull(user)){
            throw new InvalidArgumentException("Null user");
        }
        return userRepository.save(user);
    }

    @Override
    public UserResponseDTO updateUser(@Valid UpdateUserRequest request) {

        // Mendapatkan pengguna yang sedang login
        User user = getAuthenticateUser();

        // Update field hanya jika tidak null atau tidak kosong
        if (Objects.nonNull(request.getFirstName()) && !request.getFirstName().isBlank()){
            user.setFirstName(request.getFirstName());
        }
        if (Objects.nonNull(request.getLastName()) && !request.getLastName().isBlank()){
            user.setLastName(request.getLastName());
        }
        if (Objects.nonNull(request.getAddress()) && !request.getAddress().isBlank()){
            user.setAddress(request.getAddress());
        }
        if (Objects.nonNull(request.getCity()) && !request.getCity().isBlank()){
            user.setCity(request.getCity());
        }
        if (Objects.nonNull(request.getPhoneNumber()) && !request.getPhoneNumber().isBlank()){
            user.setPhoneNumber(request.getPhoneNumber());
        }

        user = userRepository.save(user);

        // Konversi ke UserResponseDTO dan kembalikan
        return userResponseConverter.converToUserResponseDTO(user);
    }

    @Override
    public void resetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new ResourceNotFoundException("User not found");
        }

        String resetCode = UUID.randomUUID().toString();    // Generate random reset code
        user.setPasswordResetCode(resetCode);
        userRepository.save(user);

        // send reset code via email
        mailService.sendResetPasswordEmail(user.getEmail(), resetCode);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByEmail(resetPasswordDTO.getEmail());
        if (user == null){
            throw new ResourceNotFoundException("User not found");
        }

        if (!resetPasswordDTO.getResetCode().equals(user.getPasswordResetCode())){
            throw new IllegalArgumentException("Invalid reset code");
        }

        // encode new password dan update user
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setPasswordResetCode(null);    // hapus code reset setelah sukses
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        User user = userRepository.findByEmail(changePasswordRequestDTO.getEmail());
        if (user == null){
            throw new ResourceNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Encode new password and update user
        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void changeUserRole(Long userId, Role newRole) {
            User user = userRepository.findById(userId)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));

            Set<Role> roles = user.getRoles();
            roles.clear();      // bisa hapus role yg sudah ada atau bs tambahkan new role tanpa menghapus role yg sudah ada
            roles.add(newRole); // tambahkan role baru

            user.setRoles(roles);

            userRepository.save(user);  // Save changes to the database



    }
}
