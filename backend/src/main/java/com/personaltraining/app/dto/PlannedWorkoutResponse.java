package com.personaltraining.app.dto;

import com.personaltraining.app.entity.SportType;
import com.personaltraining.app.entity.WorkoutStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlannedWorkoutResponse(
        Long id,
        LocalDate plannedDate,
        String title,
        String description,
        SportType sportType,
        Integer targetDurationMinutes,
        BigDecimal targetDistanceKm,
        WorkoutStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
