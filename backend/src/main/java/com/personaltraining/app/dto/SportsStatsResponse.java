package com.personaltraining.app.dto;

import com.personaltraining.app.entity.SportType;

public record SportsStatsResponse(
        SportType sportType,
        long activities
) {
}
