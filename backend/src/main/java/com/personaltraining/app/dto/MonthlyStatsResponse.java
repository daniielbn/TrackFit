package com.personaltraining.app.dto;

import java.math.BigDecimal;

public record MonthlyStatsResponse(
        String month,
        BigDecimal distanceKm,
        long durationMinutes
) {
}
