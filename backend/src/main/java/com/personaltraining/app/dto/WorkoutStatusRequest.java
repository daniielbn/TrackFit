package com.personaltraining.app.dto;

import com.personaltraining.app.entity.WorkoutStatus;
import jakarta.validation.constraints.NotNull;

public record WorkoutStatusRequest(
        @NotNull(message = "Status is required")
        WorkoutStatus status
) {
}
