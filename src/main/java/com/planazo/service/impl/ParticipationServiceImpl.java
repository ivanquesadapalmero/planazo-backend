package com.planazo.service.impl;

import com.planazo.dto.ParticipationMapper;
import com.planazo.dto.response.ParticipantResponse;
import com.planazo.dto.response.ParticipationResponse;
import com.planazo.exception.BadRequestException;
import com.planazo.exception.ForbiddenException;
import com.planazo.exception.ResourceNotFoundException;
import com.planazo.model.*;
import com.planazo.repository.ParticipationRepository;
import com.planazo.repository.PlanRepository;
import com.planazo.service.ParticipationService;
import com.planazo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final PlanRepository planRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ParticipationResponse joinPlan(Long planId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        Plan plan = findPlanById(planId);

        // Validaciones
        validateJoinPlan(plan, user);

        // Buscar si ya existe una participación previa (LEFT o REMOVED)
        Optional<Participation> existingParticipation = participationRepository.findByPlanIdAndUserId(planId, user.getId());

        Participation participation;

        if (existingParticipation.isPresent()) {
            // Reutilizar la participación existente
            participation = existingParticipation.get();
            participation.setStatus(ParticipationStatus.CONFIRMED);
            participation.setLeftAt(null);
            participation.setJoinedAt(LocalDateTime.now()); // Actualizar fecha de unión
        } else {
            // Crear nueva participación
            participation = Participation.builder()
                    .plan(plan)
                    .user(user)
                    .status(ParticipationStatus.CONFIRMED)
                    .build();
        }

        participationRepository.save(participation);

        // Incrementar contador de participantes
        plan.setCurrentParticipants(plan.getCurrentParticipants() + 1);

        // Si se llena el plan, cambiar estado a FULL
        if (plan.isFull()) {
            plan.setStatus(PlanStatus.FULL);
        }

        planRepository.save(plan);

        return ParticipationMapper.toParticipationResponse(participation);
    }

    @Override
    @Transactional
    public void leavePlan(Long planId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        Plan plan = findPlanById(planId);

        // Verificar que no sea el creador
        if (plan.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("Creator cannot leave their own plan. Cancel it instead.");
        }

        // Buscar participación
        Participation participation = participationRepository.findByPlanIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You are not participating in this plan"));

        // Verificar que esté confirmado
        if (participation.getStatus() != ParticipationStatus.CONFIRMED) {
            throw new BadRequestException("You are not actively participating in this plan");
        }

        // Actualizar participación
        participation.setStatus(ParticipationStatus.LEFT);
        participation.setLeftAt(LocalDateTime.now());
        participationRepository.save(participation);

        // Decrementar contador de participantes
        plan.setCurrentParticipants(plan.getCurrentParticipants() - 1);

        // Si estaba FULL y ahora hay espacio, cambiar a ACTIVE
        if (plan.getStatus() == PlanStatus.FULL && !plan.isFull()) {
            plan.setStatus(PlanStatus.ACTIVE);
        }

        planRepository.save(plan);
    }

    @Override
    @Transactional
    public void removeParticipant(Long planId, Long userId, String creatorEmail) {
        User creator = userService.getUserByEmail(creatorEmail);
        Plan plan = findPlanById(planId);

        // Verificar que el usuario sea el creador
        if (!plan.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenException("Only the creator can remove participants");
        }

        // No puede remover al creador
        if (userId.equals(creator.getId())) {
            throw new BadRequestException("Cannot remove yourself as creator");
        }

        // Buscar participación
        Participation participation = participationRepository.findByPlanIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not participating in this plan"));

        // Verificar que esté confirmado
        if (participation.getStatus() != ParticipationStatus.CONFIRMED) {
            throw new BadRequestException("User is not actively participating in this plan");
        }

        // Actualizar participación
        participation.setStatus(ParticipationStatus.REMOVED);
        participation.setLeftAt(LocalDateTime.now());
        participationRepository.save(participation);

        // Decrementar contador de participantes
        plan.setCurrentParticipants(plan.getCurrentParticipants() - 1);

        // Si estaba FULL y ahora hay espacio, cambiar a ACTIVE
        if (plan.getStatus() == PlanStatus.FULL && !plan.isFull()) {
            plan.setStatus(PlanStatus.ACTIVE);
        }

        planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getPlanParticipants(Long planId) {
        // Verificar que el plan existe
        findPlanById(planId);

        List<User> participants = participationRepository.findUsersByPlanIdAndStatus(
                planId,
                ParticipationStatus.CONFIRMED
        );

        return participants.stream()
                .map(ParticipationMapper::toParticipantResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParticipationResponse> getUserParticipations(String userEmail, Pageable pageable) {
        User user = userService.getUserByEmail(userEmail);

        return participationRepository.findByUserIdAndStatus(user.getId(), ParticipationStatus.CONFIRMED, pageable)
                .map(ParticipationMapper::toParticipationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserParticipating(Long planId, String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        return participationRepository.existsByPlanIdAndUserIdAndStatus(
                planId,
                user.getId(),
                ParticipationStatus.CONFIRMED
        );
    }

    private void validateJoinPlan(Plan plan, User user) {
        // Verificar que el plan esté activo
        if (plan.getStatus() != PlanStatus.ACTIVE) {
            throw new BadRequestException("This plan is not accepting new participants");
        }

        // Verificar que no esté lleno
        if (plan.isFull()) {
            throw new BadRequestException("This plan is full");
        }

        // Verificar que no sea el creador
        if (plan.getCreator().getId().equals(user.getId())) {
            throw new BadRequestException("You are already the creator of this plan");
        }

        // Verificar que no esté ya participando (solo CONFIRMED)
        boolean isAlreadyParticipating = participationRepository.existsByPlanIdAndUserIdAndStatus(
                plan.getId(),
                user.getId(),
                ParticipationStatus.CONFIRMED
        );

        if (isAlreadyParticipating) {
            throw new BadRequestException("You are already participating in this plan");
        }

        // Verificar que la fecha no haya pasado
        if (plan.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot join a plan that has already happened");
        }
    }


    private Plan findPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));
    }
}
