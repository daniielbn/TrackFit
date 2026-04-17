package com.personaltraining.app.controller;

import com.personaltraining.app.dto.MonthlyStatsResponse;
import com.personaltraining.app.dto.PaceSummaryResponse;
import com.personaltraining.app.dto.SportsStatsResponse;
import com.personaltraining.app.service.StatsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/monthly")
    public List<MonthlyStatsResponse> getMonthlyStats() {
        return statsService.getMonthlyStats();
    }

    @GetMapping("/sports")
    public List<SportsStatsResponse> getSportsStats() {
        return statsService.getSportsStats();
    }

    @GetMapping("/pace-summary")
    public PaceSummaryResponse getPaceSummary() {
        return statsService.getPaceSummary();
    }
}
