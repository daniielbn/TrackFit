package com.personaltraining.app.service;

import com.personaltraining.app.dto.DashboardSummaryResponse;
import com.personaltraining.app.dto.PlannedWorkoutResponse;
import com.personaltraining.app.entity.Activity;
import com.personaltraining.app.entity.PlannedWorkout;
import com.personaltraining.app.entity.User;
import com.personaltraining.app.entity.WorkoutStatus;
import com.personaltraining.app.repository.ActivityRepository;
import com.personaltraining.app.repository.PlannedWorkoutRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ActivityRepository activityRepository;
    private final PlannedWorkoutRepository plannedWorkoutRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(
            ActivityRepository activityRepository,
            PlannedWorkoutRepository plannedWorkoutRepository,
            CurrentUserService currentUserService
    ) {
        this.activityRepository = activityRepository;
        this.plannedWorkoutRepository = plannedWorkoutRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        User user = currentUserService.getCurrentUser();
        List<Activity> activities = activityRepository.findByUserOrderByActivityDateDescCreatedAtDesc(user);
        YearMonth currentMonth = YearMonth.now();

        long totalActivities = activities.size();
        BigDecimal totalDistance = activities.stream()
                .map(Activity::getDistanceKm)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalDuration = activities.stream()
                .mapToLong(Activity::getDurationMinutes)
                .sum();

        List<Activity> monthActivities = activities.stream()
                .filter(activity -> YearMonth.from(activity.getActivityDate()).equals(currentMonth))
                .toList();

        BigDecimal monthDistance = monthActivities.stream()
                .map(Activity::getDistanceKm)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long monthDuration = monthActivities.stream()
                .mapToLong(Activity::getDurationMinutes)
                .sum();

        List<PlannedWorkoutResponse> upcomingWorkouts = plannedWorkoutRepository
                .findTop5ByUserAndStatusAndPlannedDateGreaterThanEqualOrderByPlannedDateAsc(
                        user,
                        WorkoutStatus.PENDING,
                        LocalDate.now()
                )
                .stream()
                .map(this::toPlannedWorkoutResponse)
                .toList();

        return new DashboardSummaryResponse(
                totalActivities,
                totalDistance,
                totalDuration,
                monthActivities.size(),
                monthDistance,
                monthDuration,
                upcomingWorkouts
        );
    }

    private PlannedWorkoutResponse toPlannedWorkoutResponse(PlannedWorkout workout) {
        return new PlannedWorkoutResponse(
                workout.getId(),
                workout.getPlannedDate(),
                workout.getTitle(),
                workout.getDescription(),
                workout.getSportType(),
                workout.getTargetDurationMinutes(),
                workout.getTargetDistanceKm(),
                workout.getStatus(),
                workout.getCreatedAt(),
                workout.getUpdatedAt()
        );
    }
}
