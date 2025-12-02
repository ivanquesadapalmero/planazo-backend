package com.planazo.service;

import com.planazo.dto.request.LoginRequest;
import com.planazo.dto.response.AuthResponse;
import com.planazo.exception.UnauthorizedException;
import com.planazo.model.User;
import com.planazo.repository.UserRepository;
import com.planazo.security.JwtUtil;
import com.planazo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("test@planazo.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@planazo.com")
                .name("Test User")
                .passwordHash("hashedPassword")
                .active(true)
                .build();
    }

    @Test
    void login_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Autenticación exitosa
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), any(Long.class))).thenReturn("fake.jwt.token");

        // When
        AuthResponse response = userService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertNotNull(response.getUser());
        assertEquals("test@planazo.com", response.getUser().getEmail());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken("test@planazo.com", 1L);
    }

    @Test
    void login_InvalidCredentials_ThrowsUnauthorizedException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), any());
    }

    @Test
    void login_InactiveUser_ThrowsUnauthorizedException() {
        // Given
        user.setActive(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });

        verify(jwtUtil, never()).generateToken(anyString(), any());
    }
}
