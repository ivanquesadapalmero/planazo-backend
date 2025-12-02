package com.planazo.service;

import com.planazo.dto.request.RegisterRequest;
import com.planazo.dto.response.UserResponse;
import com.planazo.exception.ConflictException;
import com.planazo.model.User;
import com.planazo.repository.UserRepository;
import com.planazo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@planazo.com")
                .password("password123")
                .name("Test User")
                .build();
    }

    @Test
    void register_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("test@planazo.com")
                .name("Test User")
                .passwordHash("hashedPassword")
                .active(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse response = userService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("test@planazo.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertTrue(response.getActive());

        verify(userRepository, times(1)).existsByEmail("test@planazo.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsConflictException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> {
            userService.register(registerRequest);
        });

        verify(userRepository, times(1)).existsByEmail("test@planazo.com");
        verify(userRepository, never()).save(any(User.class));
    }
}

