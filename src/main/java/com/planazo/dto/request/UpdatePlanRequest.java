package com.planazo.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    private Double latitude;

    private Double longitude;

    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @Min(value = 2, message = "Max participants must be at least 2")
    @Max(value = 100, message = "Max participants cannot exceed 100")
    private Integer maxParticipants;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;
}
