package com.personaltraining.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "planned_workouts")
public class PlannedWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate plannedDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    private Integer targetDurationMinutes;

    @Column(precision = 8, scale = 2)
    private BigDecimal targetDistanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutStatus status = WorkoutStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public PlannedWorkout() {
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public Integer getTargetDurationMinutes() {
        return targetDurationMinutes;
    }

    public void setTargetDurationMinutes(Integer targetDurationMinutes) {
        this.targetDurationMinutes = targetDurationMinutes;
    }

    public BigDecimal getTargetDistanceKm() {
        return targetDistanceKm;
    }

    public void setTargetDistanceKm(BigDecimal targetDistanceKm) {
        this.targetDistanceKm = targetDistanceKm;
    }

    public WorkoutStatus getStatus() {
        return status;
    }

    public void setStatus(WorkoutStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
