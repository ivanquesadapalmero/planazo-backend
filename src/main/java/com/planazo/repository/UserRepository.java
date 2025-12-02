package com.planazo.repository;

import com.planazo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email
    Optional<User> findByEmail(String email);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar usuarios activos por nombre (búsqueda parcial)
    List<User> findByNameContainingIgnoreCaseAndActiveTrue(String nombre);
}
