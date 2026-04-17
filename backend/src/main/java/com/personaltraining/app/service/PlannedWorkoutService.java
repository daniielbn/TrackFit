package com.personaltraining.app.service;

import com.personaltraining.app.dto.PlannedWorkoutRequest;
import com.personaltraining.app.dto.PlannedWorkoutResponse;
import com.personaltraining.app.entity.PlannedWorkout;
import com.personaltraining.app.entity.User;
import com.personaltraining.app.entity.WorkoutStatus;
import com.personaltraining.app.exception.ResourceNotFoundException;
import com.personaltraining.app.repository.PlannedWorkoutRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlannedWorkoutService {

    private final PlannedWorkoutRepository plannedWorkoutRepository;
    private final CurrentUserService currentUserService;

    public PlannedWorkoutService(
            PlannedWorkoutRepository plannedWorkoutRepository,
            CurrentUserService currentUserService
    ) {
        this.plannedWorkoutRepository = plannedWorkoutRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<PlannedWorkoutResponse> findAllForCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return plannedWorkoutRepository.findByUserOrderByPlannedDateAscCreatedAtAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PlannedWorkoutResponse create(PlannedWorkoutRequest request) {
        User user = currentUserService.getCurrentUser();
        PlannedWorkout workout = new PlannedWorkout();
        workout.setUser(user);
        applyRequest(workout, request);
        return toResponse(plannedWorkoutRepository.save(workout));
    }

    @Transactional
    public PlannedWorkoutResponse update(Long id, PlannedWorkoutRequest request) {
        PlannedWorkout workout = getWorkoutForCurrentUser(id);
        applyRequest(workout, request);
        return toResponse(plannedWorkoutRepository.save(workout));
    }

    @Transactional
    public PlannedWorkoutResponse updateStatus(Long id, WorkoutStatus status) {
        PlannedWorkout workout = getWorkoutForCurrentUser(id);
        workout.setStatus(status);
        return toResponse(plannedWorkoutRepository.save(workout));
    }

    @Transactional
    public void delete(Long id) {
        PlannedWorkout workout = getWorkoutForCurrentUser(id);
        plannedWorkoutRepository.delete(workout);
    }

    private PlannedWorkout getWorkoutForCurrentUser(Long id) {
        User user = currentUserService.getCurrentUser();
        return plannedWorkoutRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Planned workout not found"));
    }

    private void applyRequest(PlannedWorkout workout, PlannedWorkoutRequest request) {
        workout.setPlannedDate(request.plannedDate());
        workout.setTitle(request.title().trim());
        workout.setDescription(request.description());
        workout.setSportType(request.sportType());
        workout.setTargetDurationMinutes(request.targetDurationMinutes());
        workout.setTargetDistanceKm(request.targetDistanceKm());
        workout.setStatus(request.status() == null ? WorkoutStatus.PENDING : request.status());
    }

    private PlannedWorkoutResponse toResponse(PlannedWorkout workout) {
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
