package com.planazo.controller;

import com.planazo.dto.request.UpdateProfileRequest;
import com.planazo.dto.response.UserResponse;
import com.planazo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Obtener el perfil del usuario autenticado
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String email = getAuthenticatedUserEmail();
        log.info("Obteniendo perfil para usuario: {}", email);

        UserResponse user = UserResponse.builder()
                .id(userService.getUserByEmail(email).getId())
                .email(userService.getUserByEmail(email).getEmail())
                .name(userService.getUserByEmail(email).getName())
                .profilePicture(userService.getUserByEmail(email).getProfilePicture())
                .bio(userService.getUserByEmail(email).getBio())
                .registrationDate(userService.getUserByEmail(email).getRegistrationDate())
                .active(userService.getUserByEmail(email).getActive())
                .build();

        return ResponseEntity.ok(user);
    }

    /**
     * Actualizar el perfil del usuario autenticado
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String email = getAuthenticatedUserEmail();
        log.info("Actualizando perfil para usuario: {}", email);

        Long userId = userService.getUserByEmail(email).getId();
        UserResponse updatedUser = userService.updateProfile(userId, request);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Eliminar la cuenta del usuario autenticado
     * DELETE /api/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteAccount() {
        String email = getAuthenticatedUserEmail();
        log.info("Eliminando cuenta para usuario: {}", email);

        Long userId = userService.getUserByEmail(email).getId();
        userService.deleteAccount(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cuenta eliminada exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener el perfil público de un usuario por ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Obteniendo perfil público de usuario ID: {}", id);

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    /**
     * Método auxiliar para obtener el email del usuario autenticado
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

