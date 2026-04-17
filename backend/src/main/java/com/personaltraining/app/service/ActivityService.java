package com.personaltraining.app.service;

import com.personaltraining.app.dto.ActivityRequest;
import com.personaltraining.app.dto.ActivityResponse;
import com.personaltraining.app.entity.Activity;
import com.personaltraining.app.entity.User;
import com.personaltraining.app.exception.ResourceNotFoundException;
import com.personaltraining.app.repository.ActivityRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CurrentUserService currentUserService;

    public ActivityService(ActivityRepository activityRepository, CurrentUserService currentUserService) {
        this.activityRepository = activityRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> findAllForCurrentUser() {
        User user = currentUserService.getCurrentUser();
        return activityRepository.findByUserOrderByActivityDateDescCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse findById(Long id) {
        Activity activity = getActivityForCurrentUser(id);
        return toResponse(activity);
    }

    @Transactional
    public ActivityResponse create(ActivityRequest request) {
        User user = currentUserService.getCurrentUser();
        Activity activity = new Activity();
        activity.setUser(user);
        applyRequest(activity, request);
        return toResponse(activityRepository.save(activity));
    }

    @Transactional
    public ActivityResponse update(Long id, ActivityRequest request) {
        Activity activity = getActivityForCurrentUser(id);
        applyRequest(activity, request);
        return toResponse(activityRepository.save(activity));
    }

    @Transactional
    public void delete(Long id) {
        Activity activity = getActivityForCurrentUser(id);
        activityRepository.delete(activity);
    }

    private Activity getActivityForCurrentUser(Long id) {
        User user = currentUserService.getCurrentUser();
        return activityRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
    }

    private void applyRequest(Activity activity, ActivityRequest request) {
        activity.setActivityDate(request.activityDate());
        activity.setSportType(request.sportType());
        activity.setTitle(request.title().trim());
        activity.setDescription(request.description());
        activity.setDurationMinutes(request.durationMinutes());
        activity.setDistanceKm(request.distanceKm());
        activity.setAveragePace(request.averagePace());
        activity.setLocation(request.location());
        activity.setNotes(request.notes());
    }

    private ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getActivityDate(),
                activity.getSportType(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getDurationMinutes(),
                activity.getDistanceKm(),
                activity.getAveragePace(),
                activity.getLocation(),
                activity.getNotes(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }
}
