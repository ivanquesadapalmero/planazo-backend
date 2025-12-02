package com.planazo.service.impl;

import com.planazo.dto.request.ForgotPasswordRequest;
import com.planazo.dto.request.ResetPasswordRequest;
import com.planazo.exception.BadRequestException;
import com.planazo.exception.ResourceNotFoundException;
import com.planazo.model.PasswordResetToken;
import com.planazo.model.User;
import com.planazo.repository.PasswordResetTokenRepository;
import com.planazo.repository.UserRepository;
import com.planazo.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        log.info("Solicitud de reset de contraseña para: {}", email);

        // Buscar usuario por email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        // Verificar que el usuario esté activo
        if (!user.getActive()) {
            throw new BadRequestException("Usuario inactivo");
        }

        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUser(user);

        // Generar nuevo token
        String token = UUID.randomUUID().toString();

        // Crear y guardar el token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // TODO: Enviar email con el token
        // Por ahora solo lo logueamos
        log.info("Token de reset generado para {}: {}", email, token);
        log.info("URL de reset: http://localhost:3000/reset-password?token={}", token);

        // En producción aquí enviarías un email con el link
        // emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Intentando resetear contraseña con token");

        // Buscar el token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        // Verificar que no haya sido usado
        if (resetToken.getUsed()) {
            throw new BadRequestException("Este token ya ha sido utilizado");
        }

        // Verificar que no haya expirado
        if (resetToken.isExpired()) {
            throw new BadRequestException("El token ha expirado");
        }

        // Actualizar la contraseña del usuario
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Marcar el token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Contraseña reseteada exitosamente para usuario: {}", user.getEmail());
    }
}
