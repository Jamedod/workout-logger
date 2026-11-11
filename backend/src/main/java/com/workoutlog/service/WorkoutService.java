package com.workoutlog.service;

import com.workoutlog.model.Exercise;
import com.workoutlog.model.PersonalRecord;
import com.workoutlog.model.Workout;
import com.workoutlog.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkoutService {

    private final WorkoutRepository repository;

    public WorkoutService(WorkoutRepository repository) {
        this.repository = repository;
    }

    // READ (all, newest first, optional search by name)
    public List<Workout> getAll(String search) {
        if (search != null && !search.isBlank()) {
            return repository.findByNameContainingIgnoreCaseOrderByDateDesc(search.trim());
        }
        return repository.findAllByOrderByDateDesc();
    }

    // READ (one)
    public Workout getById(String id) {
        return repository.findById(id).orElseThrow(() -> new WorkoutNotFoundException(id));
    }

    // CREATE
    public Workout create(Workout workout) {
        workout.setId(null); // let MongoDB generate the id
        return repository.save(workout);
    }

    // UPDATE
    public Workout update(String id, Workout updated) {
        Workout existing = getById(id);
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setNotes(updated.getNotes());
        existing.setExercises(updated.getExercises());
        return repository.save(existing);
    }

    // DELETE
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new WorkoutNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Heaviest weight per exercise (case-insensitive name match).
     * Ties on weight go to the set with more reps.
     */
    public List<PersonalRecord> getPersonalRecords() {
        Map<String, PersonalRecord> best = new LinkedHashMap<>();

        for (Workout w : repository.findAll()) {
            for (Exercise e : w.getExercises()) {
                if (e.getName() == null || e.getName().isBlank()) continue;
                String key = e.getName().trim().toLowerCase();
                PersonalRecord current = best.get(key);
                boolean better = current == null
                        || e.getWeight() > current.weight()
                        || (e.getWeight() == current.weight() && e.getReps() > current.reps());
                if (better) {
                    best.put(key, new PersonalRecord(e.getName().trim(), e.getWeight(), e.getReps(), w.getDate()));
                }
            }
        }

        List<PersonalRecord> records = new ArrayList<>(best.values());
        records.sort(Comparator.comparing(r -> r.exercise().toLowerCase()));
        return records;
    }
}
