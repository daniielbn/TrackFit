package com.personaltraining.app.repository;

import com.personaltraining.app.entity.Activity;
import com.personaltraining.app.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserOrderByActivityDateDescCreatedAtDesc(User user);

    Optional<Activity> findByIdAndUser(Long id, User user);
}
