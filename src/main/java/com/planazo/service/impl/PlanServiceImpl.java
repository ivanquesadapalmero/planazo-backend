package com.planazo.service;

import com.planazo.dto.PlanMapper;
import com.planazo.dto.request.CreatePlanRequest;
import com.planazo.dto.request.UpdatePlanRequest;
import com.planazo.dto.response.PlanResponse;
import com.planazo.exception.ForbiddenException;
import com.planazo.exception.ResourceNotFoundException;
import com.planazo.model.Category;
import com.planazo.model.Plan;
import com.planazo.model.PlanStatus;
import com.planazo.model.User;
import com.planazo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    @Transactional
    public PlanResponse createPlan(CreatePlanRequest request, String creatorEmail) {
        User creator = userService.getUserByEmail(creatorEmail);
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        Plan plan = PlanMapper.toPlan(request, category, creator);
        Plan savedPlan = planRepository.save(plan);

        return PlanMapper.toPlanResponse(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long id) {
        Plan plan = findPlanById(id);
        return PlanMapper.toPlanResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanResponse> getAllPlans(Pageable pageable) {
        return planRepository.findAll(pageable)
                .map(PlanMapper::toPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanResponse> getUpcomingPlans(Pageable pageable) {
        return planRepository.findUpcomingPlans(PlanStatus.ACTIVE, LocalDateTime.now(), pageable)
                .map(PlanMapper::toPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanResponse> getPlansByCategory(Long categoryId, Pageable pageable) {
        return planRepository.findByCategoryIdAndStatus(categoryId, PlanStatus.ACTIVE, pageable)
                .map(PlanMapper::toPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanResponse> getPlansByCreator(Long creatorId, Pageable pageable) {
        return planRepository.findByCreatorId(creatorId, pageable)
                .map(PlanMapper::toPlanResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlanResponse> searchPlansByLocation(String location, Pageable pageable) {
        return planRepository.findByLocationContainingIgnoreCaseAndStatus(location, PlanStatus.ACTIVE, pageable)
                .map(PlanMapper::toPlanResponse);
    }

    @Override
    @Transactional
    public PlanResponse updatePlan(Long id, UpdatePlanRequest request, String userEmail) {
        Plan plan = findPlanById(id);
        User user = userService.getUserByEmail(userEmail);

        // Verificar que el usuario sea el creador
        if (!plan.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only update your own plans");
        }

        PlanMapper.updatePlanFromRequest(plan, request);
        Plan updatedPlan = planRepository.save(plan);

        return PlanMapper.toPlanResponse(updatedPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id, String userEmail) {
        Plan plan = findPlanById(id);
        User user = userService.getUserByEmail(userEmail);

        // Verificar que el usuario sea el creador
        if (!plan.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only delete your own plans");
        }

        planRepository.delete(plan);
    }

    @Override
    @Transactional
    public void cancelPlan(Long id, String userEmail) {
        Plan plan = findPlanById(id);
        User user = userService.getUserByEmail(userEmail);

        // Verificar que el usuario sea el creador
        if (!plan.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only cancel your own plans");
        }

        plan.setStatus(PlanStatus.CANCELLED);
        planRepository.save(plan);
    }

    private Plan findPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
    }
}
