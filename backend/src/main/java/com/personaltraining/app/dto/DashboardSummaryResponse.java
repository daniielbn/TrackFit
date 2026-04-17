package com.personaltraining.app.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        long totalActivities,
        BigDecimal totalDistanceKm,
        long totalDurationMinutes,
        long activitiesThisMonth,
        BigDecimal distanceThisMonth,
        long durationThisMonth,
        List<PlannedWorkoutResponse> upcomingWorkouts
) {
}
