package com.personaltraining.app.dto;

import com.personaltraining.app.entity.SportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivityRequest(
        @NotNull(message = "Activity date is required")
        LocalDate activityDate,

        @NotNull(message = "Sport type is required")
        SportType sportType,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be greater than zero")
        Integer durationMinutes,

        @NotNull(message = "Distance is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Distance cannot be negative")
        BigDecimal distanceKm,

        @DecimalMin(value = "0.0", inclusive = false, message = "Average pace must be greater than zero")
        BigDecimal averagePace,

        String location,
        String notes
) {
}
