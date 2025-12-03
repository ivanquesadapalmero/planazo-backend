package com.planazo.repository;

import com.planazo.model.Participation;
import com.planazo.model.ParticipationStatus;
import com.planazo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // Verificar si un usuario ya está en un plan
    boolean existsByPlanIdAndUserIdAndStatus(Long planId, Long userId, ParticipationStatus status);

    // Encontrar participación de un usuario en un plan
    Optional<Participation> findByPlanIdAndUserId(Long planId, Long userId);

    // Listar participaciones confirmadas de un plan
    List<Participation> findByPlanIdAndStatus(Long planId, ParticipationStatus status);

    // Listar planes a los que un usuario se ha unido (confirmados)
    Page<Participation> findByUserIdAndStatus(Long userId, ParticipationStatus status, Pageable pageable);

    // Contar participantes confirmados en un plan
    long countByPlanIdAndStatus(Long planId, ParticipationStatus status);

    // Obtener todos los usuarios participantes de un plan
    @Query("SELECT p.user FROM Participation p WHERE p.plan.id = :planId AND p.status = :status")
    List<User> findUsersByPlanIdAndStatus(@Param("planId") Long planId, @Param("status") ParticipationStatus status);
}
