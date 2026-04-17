package com.personaltraining.app.dto;

import com.personaltraining.app.entity.SportType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        LocalDate activityDate,
        SportType sportType,
        String title,
        String description,
        Integer durationMinutes,
        BigDecimal distanceKm,
        BigDecimal averagePace,
        String location,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
