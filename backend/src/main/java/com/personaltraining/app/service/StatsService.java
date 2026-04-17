package com.personaltraining.app.service;

import com.personaltraining.app.dto.MonthlyStatsResponse;
import com.personaltraining.app.dto.PaceSummaryResponse;
import com.personaltraining.app.dto.SportsStatsResponse;
import com.personaltraining.app.entity.Activity;
import com.personaltraining.app.entity.SportType;
import com.personaltraining.app.entity.User;
import com.personaltraining.app.repository.ActivityRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsService {

    private final ActivityRepository activityRepository;
    private final CurrentUserService currentUserService;

    public StatsService(ActivityRepository activityRepository, CurrentUserService currentUserService) {
        this.activityRepository = activityRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<MonthlyStatsResponse> getMonthlyStats() {
        List<Activity> activities = findCurrentUserActivities();
        Map<YearMonth, MonthlyTotals> totalsByMonth = new TreeMap<>();

        for (Activity activity : activities) {
            YearMonth month = YearMonth.from(activity.getActivityDate());
            MonthlyTotals totals = totalsByMonth.computeIfAbsent(month, ignored -> new MonthlyTotals());
            totals.distanceKm = totals.distanceKm.add(activity.getDistanceKm());
            totals.durationMinutes += activity.getDurationMinutes();
        }

        return totalsByMonth.entrySet()
                .stream()
                .map(entry -> new MonthlyStatsResponse(
                        entry.getKey().toString(),
                        entry.getValue().distanceKm,
                        entry.getValue().durationMinutes
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SportsStatsResponse> getSportsStats() {
        List<Activity> activities = findCurrentUserActivities();
        Map<SportType, Long> totalsBySport = new EnumMap<>(SportType.class);

        for (Activity activity : activities) {
            totalsBySport.merge(activity.getSportType(), 1L, Long::sum);
        }

        return totalsBySport.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().name()))
                .map(entry -> new SportsStatsResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PaceSummaryResponse getPaceSummary() {
        List<BigDecimal> paces = findCurrentUserActivities().stream()
                .map(Activity::getAveragePace)
                .filter(pace -> pace != null)
                .toList();

        if (paces.isEmpty()) {
            return new PaceSummaryResponse(null);
        }

        BigDecimal total = paces.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = total.divide(BigDecimal.valueOf(paces.size()), 2, RoundingMode.HALF_UP);
        return new PaceSummaryResponse(average);
    }

    private List<Activity> findCurrentUserActivities() {
        User user = currentUserService.getCurrentUser();
        return activityRepository.findByUserOrderByActivityDateDescCreatedAtDesc(user);
    }

    private static class MonthlyTotals {
        private BigDecimal distanceKm = BigDecimal.ZERO;
        private long durationMinutes;
    }
}
