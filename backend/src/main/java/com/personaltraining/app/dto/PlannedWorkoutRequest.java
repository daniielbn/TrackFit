package com.personaltraining.app.dto;

import com.personaltraining.app.entity.SportType;
import com.personaltraining.app.entity.WorkoutStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PlannedWorkoutRequest(
        @NotNull(message = "Planned date is required")
        LocalDate plannedDate,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Sport type is required")
        SportType sportType,

        @Positive(message = "Target duration must be greater than zero")
        Integer targetDurationMinutes,

        @DecimalMin(value = "0.0", inclusive = false, message = "Target distance must be greater than zero")
        BigDecimal targetDistanceKm,

        WorkoutStatus status
) {
}
