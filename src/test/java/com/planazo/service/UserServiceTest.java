package com.planazo.service;

import com.planazo.dto.request.LoginRequest;
import com.planazo.dto.request.RegisterRequest;
import com.planazo.dto.response.AuthResponse;
import com.planazo.dto.response.UserResponse;
import com.planazo.exception.ConflictException;
import com.planazo.exception.UnauthorizedException;
import com.planazo.model.User;
import com.planazo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos antes de cada test
        userRepository.deleteAll();

        // Preparar datos de prueba
        registerRequest = RegisterRequest.builder()
                .email("test@planazo.com")
                .password("password123")
                .name("Test User")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@planazo.com")
                .password("password123")
                .build();
    }

    @Test
    void register_Success() {
        // When
        UserResponse response = userService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("test@planazo.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertTrue(response.getActive());
        assertNotNull(response.getRegistrationDate());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsConflictException() {
        // Given - Registrar primero
        userService.register(registerRequest);

        // When & Then - Intentar registrar de nuevo con el mismo email
        assertThrows(ConflictException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void register_EmailWithUppercase_SavesInLowercase() {
        // Given
        registerRequest.setEmail("TEST@PLANAZO.COM");

        // When
        UserResponse response = userService.register(registerRequest);

        // Then
        assertEquals("test@planazo.com", response.getEmail());
    }

    @Test
    void login_Success() {
        // Given - Registrar usuario primero
        userService.register(registerRequest);

        // When
        AuthResponse response = userService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getType());
        assertNotNull(response.getUser());
        assertEquals("test@planazo.com", response.getUser().getEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsUnauthorizedException() {
        // Given - Registrar usuario primero
        userService.register(registerRequest);

        // Cambiar la contraseña a una incorrecta
        loginRequest.setPassword("wrongpassword");

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void login_UserNotExists_ThrowsUnauthorizedException() {
        // When & Then - Intentar login sin registrar
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void login_InactiveUser_ThrowsUnauthorizedException() {
        // Given - Registrar y desactivar usuario
        userService.register(registerRequest);
        User user = userRepository.findByEmail("test@planazo.com").orElseThrow();
        user.setActive(false);
        userRepository.save(user);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void getUserByEmail_Success() {
        // Given
        userService.register(registerRequest);

        // When
        User user = userService.getUserByEmail("test@planazo.com");

        // Then
        assertNotNull(user);
        assertEquals("test@planazo.com", user.getEmail());
        assertEquals("Test User", user.getName());
    }

    @Test
    void updateProfile_Success() {
        // Given
        UserResponse registeredUser = userService.register(registerRequest);

        com.planazo.dto.request.UpdateProfileRequest updateRequest =
                com.planazo.dto.request.UpdateProfileRequest.builder()
                        .name("Test User Updated")
                        .bio("Nueva biografía de prueba")
                        .profilePicture("https://example.com/foto.jpg")
                        .build();

        // When
        UserResponse updatedUser = userService.updateProfile(registeredUser.getId(), updateRequest);

        // Then
        assertNotNull(updatedUser);
        assertEquals("Test User Updated", updatedUser.getName());
        assertEquals("Nueva biografía de prueba", updatedUser.getBio());
        assertEquals("https://example.com/foto.jpg", updatedUser.getProfilePicture());
    }

    @Test
    void deleteAccount_Success() {
        // Given
        UserResponse registeredUser = userService.register(registerRequest);

        // When
        userService.deleteAccount(registeredUser.getId());

        // Then
        User user = userRepository.findById(registeredUser.getId()).orElseThrow();
        assertFalse(user.getActive()); // Soft delete - usuario desactivado
    }
}
