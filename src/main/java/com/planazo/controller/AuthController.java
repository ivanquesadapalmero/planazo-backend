package com.planazo.controller;

import com.planazo.dto.request.LoginRequest;
import com.planazo.dto.request.RegisterRequest;
import com.planazo.dto.response.AuthResponse;
import com.planazo.dto.response.UserResponse;
import com.planazo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Petición de registro recibida para email: {}", request.getEmail());

        UserResponse userResponse = userService.register(request);

        LoginRequest loginRequest = LoginRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        AuthResponse authResponse = userService.login(loginRequest);

        log.info("Usuario registrado y logueado exitosamente: {}", userResponse.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Petición de login recibida para email: {}", request.getEmail());

        AuthResponse response = userService.login(request);

        log.info("Login exitoso para: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyToken() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token válido");
        response.put("status", "authenticated");
        return ResponseEntity.ok(response);
    }
}

