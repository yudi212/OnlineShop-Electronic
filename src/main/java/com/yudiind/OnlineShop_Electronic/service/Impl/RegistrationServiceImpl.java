package com.yudiind.OnlineShop_Electronic.service.Impl;

import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.CaptchResponse;
import com.yudiind.OnlineShop_Electronic.model.dto.UserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.Role;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.UserRepository;
import com.yudiind.OnlineShop_Electronic.service.RegistrationService;
import com.yudiind.OnlineShop_Electronic.util.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final UserMapper userMapper;

    @Autowired
    public RegistrationServiceImpl(UserRepository userRepository, MailService mailService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User registration(UserRequest userRequest) throws ResponseStatusException {

        // Cek apakah email sudah digunakan
        User existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser != null){
            throw new IllegalArgumentException("Email already used");
        }

        // Validasi dan mapping UserRequest ke User
        User newUser = userMapper.mapUserRequestToUser(userRequest);

        // Simpan user baru ke database
        userRepository.save(newUser);
        log.info("User saved successfully: {}", newUser);

       /**
            KALAU VALIDASI DAN MAPPING MENGGUNAKAN ModelMapper
        // validasi password
        if (userRequest.getPassword() != null && !userRequest.getPassword().equals(userRequest.getPassword2())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password do not match");
        }
        // validasi email
        if (userRepository.findByEmail(userRequest.getEmail()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email in use");
        }

        // Membuat dan Menyimpan Pengguna Baru:
        User user = modelMapper.map(userRequest, User.class);
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Saving user entity: {}", user);
        userRepository.save(user);
        log.info("User saved successfully: {}", user);
        */

        /**
         Cara lain mengirimkan link aktivasi account ke email user
         private void sendVerificationEmail(User user, String token, String siteURL) throws MessagingException, UnsupportedEncodingException {
         String subject = "Please verify your registration";
         String senderName = "Ecommerce Service";
         String verifyURL = siteURL + "/verify?token=" + token;

         MimeMessage message = mailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message);

         helper.setFrom("your-email@example.com", senderName);
         helper.setTo(user.getEmail());
         helper.setSubject(subject);

         String content = "<p>Hello, " + user.getUsername() + "</p>"
         + "<p>Please click the link below to verify your registration:</p>"
         + "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>"
         + "<p>Thank you,<br>Ecommerce Service</p>";

         helper.setText(content, true);
         mailSender.send(message);
         }
         */

        // Mengirim Email Aktivasi
        // - http://localhost:8080/registration/activate Ini adalah URL dasar yang akan digunakan untuk aktivasi akun. Ketika pengguna mengklik tautan ini, mereka akan diarahkan ke endpoint /registration/activate di aplikasi Anda.
        // - getActivationCode() pada objek User untuk mengambil kode aktivasi yang dihasilkan saat pengguna mendaftar. Kode ini kemudian ditambahkan ke URL sebagai nilai dari parameter code.
        // - activationLink: Variabel ini menyimpan URL lengkap yang akan dikirimkan melalui email kepada pengguna.
        // - Misalnya, jika user.getActivationCode() mengembalikan 12345-abcde, maka activationLink akan menjadi: http://localhost:8080/registration/activate?code=12345-abcde
        // - Kesimpulan
        //          Tautan aktivasi yang dibentuk dengan cara ini memungkinkan aplikasi Anda untuk mengirimkan kode aktivasi kepada pengguna dan memverifikasi akun mereka dengan aman dan efisien.
        String activationLink = "http://localhost:8080/registration/activate?code=" + newUser.getActivationCode();
        mailService.sendMail(newUser.getEmail(), "Activation Link", "klik link berikut ini untuk mengaktifkan akun anda: " + activationLink );


        /**
         * Mengirim email aktifasi mengguanakan captcha, thmyeleaf dan html
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", user.getFirstName());
        attributes.put("activationCode", "/registration/activate/" + user.getActivationCode());
        mailService.sendMessageHtml(user.getEmail(), "Activation code", "registration-template", attributes );
         */
        return newUser;
    }

    @Override
    public Optional<User> activateEmailCode(String code) {
        Optional<User> user =userRepository.findByActivationCode(code);
        if (user.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    @Override
    @Transactional
    public void activateUser(String activationCode) {
            Optional<User> userOptional = userRepository.findByActivationCode(activationCode);
            if (userOptional.isEmpty()){
                throw new ResourceNotFoundException("Activation code is invalid");
            }

            User user = userOptional.get();
            user.setActive(true);           // setelah user diaktifkan
            user.setActivationCode(null);   // maka hapus activation code nya
            userRepository.save(user);
    }


}
