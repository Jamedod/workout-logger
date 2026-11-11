package com.workoutlog.repository;

import com.workoutlog.model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WorkoutRepository extends MongoRepository<Workout, String> {

    // Spring Data builds these queries from the method names
    List<Workout> findAllByOrderByDateDesc();

    List<Workout> findByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
