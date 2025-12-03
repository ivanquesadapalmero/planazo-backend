package com.planazo.service;

import com.planazo.dto.request.CreatePlanRequest;
import com.planazo.dto.request.UpdatePlanRequest;
import com.planazo.dto.response.PlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanService {

    PlanResponse createPlan(CreatePlanRequest request, String creatorEmail);

    PlanResponse getPlanById(Long id);

    Page<PlanResponse> getAllPlans(Pageable pageable);

    Page<PlanResponse> getUpcomingPlans(Pageable pageable);

    Page<PlanResponse> getPlansByCategory(Long categoryId, Pageable pageable);

    Page<PlanResponse> getPlansByCreator(Long creatorId, Pageable pageable);

    Page<PlanResponse> searchPlansByLocation(String location, Pageable pageable);

    PlanResponse updatePlan(Long id, UpdatePlanRequest request, String userEmail);

    void deletePlan(Long id, String userEmail);

    void cancelPlan(Long id, String userEmail);
}

