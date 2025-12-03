package com.planazo.repository;

import com.planazo.model.Plan;
import com.planazo.model.PlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    // Buscar planes por estado
    Page<Plan> findByStatus(PlanStatus status, Pageable pageable);

    // Buscar planes activos
    Page<Plan> findByStatusAndEventDateAfter(PlanStatus status, LocalDateTime date, Pageable pageable);

    // Buscar planes por categoría
    Page<Plan> findByCategoryIdAndStatus(Long categoryId, PlanStatus status, Pageable pageable);

    // Buscar planes creados por un usuario
    Page<Plan> findByCreatorId(Long creatorId, Pageable pageable);

    // Buscar planes por ubicación (búsqueda simple)
    Page<Plan> findByLocationContainingIgnoreCaseAndStatus(String location, PlanStatus status, Pageable pageable);

    // Buscar planes próximos (activos y con fecha futura)
    @Query("SELECT p FROM Plan p WHERE p.status = :status AND p.eventDate > :now ORDER BY p.eventDate ASC")
    Page<Plan> findUpcomingPlans(@Param("status") PlanStatus status, @Param("now") LocalDateTime now, Pageable pageable);

    // Contar planes activos de un usuario
    long countByCreatorIdAndStatus(Long creatorId, PlanStatus status);
}
