package com.planazo.dto.response;

import com.planazo.model.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationResponse {
    private Long id;
    private Long planId;
    private UserResponse user;
    private ParticipationStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
