package com.planazo.service;

import com.planazo.dto.response.ParticipantResponse;
import com.planazo.dto.response.ParticipationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParticipationService {

    ParticipationResponse joinPlan(Long planId, String userEmail);

    void leavePlan(Long planId, String userEmail);

    void removeParticipant(Long planId, Long userId, String creatorEmail);

    List<ParticipantResponse> getPlanParticipants(Long planId);

    Page<ParticipationResponse> getUserParticipations(String userEmail, Pageable pageable);

    boolean isUserParticipating(Long planId, String userEmail);
}
