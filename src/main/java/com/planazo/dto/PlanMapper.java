package com.planazo.dto;

import com.planazo.dto.request.CreatePlanRequest;
import com.planazo.dto.request.UpdatePlanRequest;
import com.planazo.dto.response.PlanResponse;
import com.planazo.model.Category;
import com.planazo.model.Plan;
import com.planazo.model.PlanStatus;
import com.planazo.model.User;

public class PlanMapper {

    public static PlanResponse toPlanResponse(Plan plan) {
        if (plan == null) {
            return null;
        }

        return PlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .category(CategoryMapper.toCategoryResponse(plan.getCategory()))
                .creator(UserMapper.toUserResponse(plan.getCreator()))
                .location(plan.getLocation())
                .latitude(plan.getLatitude())
                .longitude(plan.getLongitude())
                .eventDate(plan.getEventDate())
                .maxParticipants(plan.getMaxParticipants())
                .currentParticipants(plan.getCurrentParticipants())
                .status(plan.getStatus())
                .imageUrl(plan.getImageUrl())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .isFull(plan.isFull())
                .canJoin(plan.canJoin())
                .build();
    }

    public static Plan toPlan(CreatePlanRequest request, Category category, User creator) {
        return Plan.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .creator(creator)
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .eventDate(request.getEventDate())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1) // El creador es el primer participante
                .status(PlanStatus.ACTIVE)
                .imageUrl(request.getImageUrl())
                .build();
    }

    public static void updatePlanFromRequest(Plan plan, UpdatePlanRequest request) {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            plan.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription().trim());
        }
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            plan.setLocation(request.getLocation().trim());
        }
        if (request.getLatitude() != null) {
            plan.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            plan.setLongitude(request.getLongitude());
        }
        if (request.getEventDate() != null) {
            plan.setEventDate(request.getEventDate());
        }
        if (request.getMaxParticipants() != null) {
            plan.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getImageUrl() != null) {
            plan.setImageUrl(request.getImageUrl().trim());
        }
    }
}
