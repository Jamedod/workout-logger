package com.workoutlog.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * An exercise embedded inside a Workout document (not its own collection).
 */
public class Exercise {

    @NotBlank(message = "Exercise name is required")
    private String name;

    @Min(value = 1, message = "Sets must be at least 1")
    private int sets;

    @Min(value = 1, message = "Reps must be at least 1")
    private int reps;

    @Min(value = 0, message = "Weight cannot be negative")
    private double weight; // in lbs (or kg — just be consistent)

    public Exercise() {}

    public Exercise(String name, int sets, int reps, double weight) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
