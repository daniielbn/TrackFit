package com.personaltraining.app.dto;

import java.math.BigDecimal;

public record PaceSummaryResponse(
        BigDecimal averagePace
) {
}
