package com.planazo.dto.response;

import com.planazo.model.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private Long id;
    private String title;
    private String description;
    private CategoryResponse category;
    private UserResponse creator;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime eventDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private PlanStatus status;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isFull;
    private Boolean canJoin;
}
