package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.CaptchResponse;
import com.yudiind.OnlineShop_Electronic.model.dto.UserRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.UserRepository;
import com.yudiind.OnlineShop_Electronic.service.Impl.MailService;
import com.yudiind.OnlineShop_Electronic.service.Impl.RegistrationServiceImpl;
import com.yudiind.OnlineShop_Electronic.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void testRegistrationSuccess() {

        // Prepare test data
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("test");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setPassword2("password123");

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setPassword(userRequest.getPassword());

        // Define behavior of mocks
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(null);
        when(userMapper.mapUserRequestToUser(userRequest)).thenReturn(user);

        // Invoke the method
        User result = registrationService.registration(userRequest);

        // Verify the results
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        assertFalse(result.isActive());

        verify(userRepository, times(1)).save(result);
    }
}
