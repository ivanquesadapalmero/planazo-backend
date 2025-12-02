package com.planazo.service;

import com.planazo.dto.request.RegisterRequest;
import com.planazo.dto.request.UpdateProfileRequest;
import com.planazo.dto.response.UserResponse;
import com.planazo.model.User;

public interface UserService {

    // Registrar un nuevo usuario
    UserResponse register(RegisterRequest request);

    // Obtener usuario por ID
    UserResponse getUserById(Long id);

    // Obtener usuario por email
    User getUserByEmail(String email);

    // Actualizar perfil de usuario
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    // Eliminar cuenta de usuario
    void deleteAccount(Long userId);
}

