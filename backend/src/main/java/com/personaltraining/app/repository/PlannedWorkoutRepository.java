package com.personaltraining.app.repository;

import com.personaltraining.app.entity.PlannedWorkout;
import com.personaltraining.app.entity.User;
import com.personaltraining.app.entity.WorkoutStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannedWorkoutRepository extends JpaRepository<PlannedWorkout, Long> {

    List<PlannedWorkout> findByUserOrderByPlannedDateAscCreatedAtAsc(User user);

    Optional<PlannedWorkout> findByIdAndUser(Long id, User user);

    List<PlannedWorkout> findTop5ByUserAndStatusAndPlannedDateGreaterThanEqualOrderByPlannedDateAsc(
            User user,
            WorkoutStatus status,
            LocalDate plannedDate
    );
}
