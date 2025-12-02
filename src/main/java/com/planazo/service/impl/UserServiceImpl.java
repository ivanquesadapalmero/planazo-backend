package com.planazo.service.impl;

import com.planazo.dto.UserMapper;
import com.planazo.dto.request.LoginRequest;
import com.planazo.dto.request.RegisterRequest;
import com.planazo.dto.request.UpdateProfileRequest;
import com.planazo.dto.response.AuthResponse;
import com.planazo.dto.response.UserResponse;
import com.planazo.exception.BadRequestException;
import com.planazo.exception.ConflictException;
import com.planazo.exception.ResourceNotFoundException;
import com.planazo.exception.UnauthorizedException;
import com.planazo.model.User;
import com.planazo.repository.UserRepository;
import com.planazo.security.JwtUtil;
import com.planazo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.debug("Intentando registrar usuario con email: {}", request.getEmail());

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new ConflictException("El email ya está registrado");
        }

        // Validar formato de email
        if (!isValidEmail(request.getEmail())) {
            throw new BadRequestException("Formato de email inválido");
        }

        // Crear el usuario
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName().trim())
                .active(true)
                .build();

        // Guardar en base de datos
        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());

        // Convertir a DTO y retornar
        return UserMapper.toUserResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Intentando login para email: {}", request.getEmail());

        try {
            // Autenticar usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getPassword()
                    )
            );

            // Si llegamos aquí, la autenticación fue exitosa
            log.debug("Autenticación exitosa para: {}", request.getEmail());

        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para email: {}", request.getEmail());
            throw new UnauthorizedException("Email o contraseña incorrectos");
        }

        // Buscar el usuario en la base de datos
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Email o contraseña incorrectos"));

        // Verificar que el usuario esté activo
        if (!user.getActive()) {
            log.warn("Intento de login de usuario inactivo: {}", request.getEmail());
            throw new UnauthorizedException("Usuario inactivo");
        }

        // Generar token JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        log.info("Login exitoso para usuario ID: {}", user.getId());

        // Construir y retornar la respuesta
        UserResponse userResponse = UserMapper.toUserResponse(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Buscando usuario por ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        return UserMapper.toUserResponse(user);
    }

    @Override
    public User getUserByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.debug("Actualizando perfil del usuario ID: {}", userId);

        // Buscar el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Actualizar campos usando el mapper
        UserMapper.updateUserFromRequest(user, request);

        // Guardar cambios
        User updatedUser = userRepository.save(user);
        log.info("Perfil actualizado para usuario ID: {}", userId);

        return UserMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        log.debug("Eliminando cuenta del usuario ID: {}", userId);

        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Soft delete: marcar como inactivo
        user.setActive(false);
        userRepository.save(user);

        log.info("Cuenta eliminada (desactivada) para usuario ID: {}", userId);
    }

    // Método auxiliar para validar email
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
