package com.workoutlog.service;

public class WorkoutNotFoundException extends RuntimeException {
    public WorkoutNotFoundException(String id) {
        super("Workout not found: " + id);
    }
}
