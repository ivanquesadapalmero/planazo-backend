package com.planazo.controller;

import com.planazo.dto.request.CreatePlanRequest;
import com.planazo.dto.request.UpdatePlanRequest;
import com.planazo.dto.response.PlanResponse;
import com.planazo.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(
            @Valid @RequestBody CreatePlanRequest request,
            Authentication authentication
    ) {
        PlanResponse plan = planService.createPlan(request, authentication.getName());
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id) {
        PlanResponse plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<Page<PlanResponse>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<PlanResponse> plans = planService.getAllPlans(pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<PlanResponse>> getUpcomingPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanResponse> plans = planService.getUpcomingPlans(pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PlanResponse>> getPlansByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanResponse> plans = planService.getPlansByCategory(categoryId, pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<Page<PlanResponse>> getPlansByCreator(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PlanResponse> plans = planService.getPlansByCreator(creatorId, pageable);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PlanResponse>> searchPlansByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlanResponse> plans = planService.searchPlansByLocation(location, pageable);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlanRequest request,
            Authentication authentication
    ) {
        PlanResponse plan = planService.updatePlan(id, request, authentication.getName());
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long id,
            Authentication authentication
    ) {
        planService.deletePlan(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelPlan(
            @PathVariable Long id,
            Authentication authentication
    ) {
        planService.cancelPlan(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
