package com.planazo.controller;

import com.planazo.dto.response.ParticipantResponse;
import com.planazo.dto.response.ParticipationResponse;
import com.planazo.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping("/plans/{planId}/join")
    public ResponseEntity<ParticipationResponse> joinPlan(
            @PathVariable Long planId,
            Authentication authentication
    ) {
        ParticipationResponse participation = participationService.joinPlan(planId, authentication.getName());
        return new ResponseEntity<>(participation, HttpStatus.CREATED);
    }

    @DeleteMapping("/plans/{planId}/leave")
    public ResponseEntity<Void> leavePlan(
            @PathVariable Long planId,
            Authentication authentication
    ) {
        participationService.leavePlan(planId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/plans/{planId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long planId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        participationService.removeParticipant(planId, userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plans/{planId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getPlanParticipants(@PathVariable Long planId) {
        List<ParticipantResponse> participants = participationService.getPlanParticipants(planId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/my-participations")
    public ResponseEntity<Page<ParticipationResponse>> getMyParticipations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "joinedAt"));
        Page<ParticipationResponse> participations = participationService.getUserParticipations(
                authentication.getName(),
                pageable
        );
        return ResponseEntity.ok(participations);
    }

    @GetMapping("/plans/{planId}/check")
    public ResponseEntity<Boolean> checkParticipation(
            @PathVariable Long planId,
            Authentication authentication
    ) {
        boolean isParticipating = participationService.isUserParticipating(planId, authentication.getName());
        return ResponseEntity.ok(isParticipating);
    }
}
