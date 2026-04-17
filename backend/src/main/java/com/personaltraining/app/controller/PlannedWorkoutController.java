package com.personaltraining.app.controller;

import com.personaltraining.app.dto.PlannedWorkoutRequest;
import com.personaltraining.app.dto.PlannedWorkoutResponse;
import com.personaltraining.app.dto.WorkoutStatusRequest;
import com.personaltraining.app.service.PlannedWorkoutService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planned-workouts")
public class PlannedWorkoutController {

    private final PlannedWorkoutService plannedWorkoutService;

    public PlannedWorkoutController(PlannedWorkoutService plannedWorkoutService) {
        this.plannedWorkoutService = plannedWorkoutService;
    }

    @GetMapping
    public List<PlannedWorkoutResponse> findAll() {
        return plannedWorkoutService.findAllForCurrentUser();
    }

    @PostMapping
    public ResponseEntity<PlannedWorkoutResponse> create(@Valid @RequestBody PlannedWorkoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plannedWorkoutService.create(request));
    }

    @PutMapping("/{id}")
    public PlannedWorkoutResponse update(@PathVariable Long id, @Valid @RequestBody PlannedWorkoutRequest request) {
        return plannedWorkoutService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public PlannedWorkoutResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutStatusRequest request
    ) {
        return plannedWorkoutService.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plannedWorkoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
