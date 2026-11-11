package com.workoutlog.controller;

import com.workoutlog.model.PersonalRecord;
import com.workoutlog.model.Workout;
import com.workoutlog.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkoutController {

    private final WorkoutService service;

    public WorkoutController(WorkoutService service) {
        this.service = service;
    }

    // Simple health check, handy for confirming the Railway deploy works
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    // GET /api/workouts?search=push
    @GetMapping("/workouts")
    public List<Workout> getAll(@RequestParam(required = false) String search) {
        return service.getAll(search);
    }

    // GET /api/workouts/{id}
    @GetMapping("/workouts/{id}")
    public Workout getById(@PathVariable String id) {
        return service.getById(id);
    }

    // POST /api/workouts
    @PostMapping("/workouts")
    @ResponseStatus(HttpStatus.CREATED)
    public Workout create(@Valid @RequestBody Workout workout) {
        return service.create(workout);
    }

    // PUT /api/workouts/{id}
    @PutMapping("/workouts/{id}")
    public Workout update(@PathVariable String id, @Valid @RequestBody Workout workout) {
        return service.update(id, workout);
    }

    // DELETE /api/workouts/{id}
    @DeleteMapping("/workouts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // GET /api/stats/prs
    @GetMapping("/stats/prs")
    public List<PersonalRecord> personalRecords() {
        return service.getPersonalRecords();
    }
}
