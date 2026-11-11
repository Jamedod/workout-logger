package com.workoutlog.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * One workout session. Stored in the "workouts" collection, with its
 * exercises embedded as an array inside the same document.
 */
@Document(collection = "workouts")
public class Workout {

    @Id
    private String id;

    @NotBlank(message = "Workout name is required")
    private String name;          // e.g. "Push Day"

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Min(value = 0, message = "Duration cannot be negative")
    private int durationMinutes;

    private String notes;

    @Valid
    private List<Exercise> exercises = new ArrayList<>();

    public Workout() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }
}
