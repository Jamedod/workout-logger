package com.workoutlog.model;

import java.time.LocalDate;

/**
 * Heaviest weight lifted for an exercise across all workouts.
 * Computed on the fly, not stored in MongoDB.
 */
public record PersonalRecord(String exercise, double weight, int reps, LocalDate date) {}
