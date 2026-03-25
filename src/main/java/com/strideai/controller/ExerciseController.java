package com.strideai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strideai.dto.WorkoutDTO;
import com.strideai.model.Exercise;
import com.strideai.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired private ExerciseRepository exerciseRepo;
    @Autowired private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<WorkoutDTO.ExerciseResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        List<Exercise> exercises;
        if (category != null && !category.isBlank()) {
            exercises = exerciseRepo.findByCategoryAndActiveTrue(category);
        } else if (difficulty != null && !difficulty.isBlank()) {
            exercises = exerciseRepo.findByDifficultyAndActiveTrue(difficulty);
        } else {
            exercises = exerciseRepo.findByActiveTrue();
        }
        return ResponseEntity.ok(exercises.stream().map(WorkoutDTO.ExerciseResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO.ExerciseResponse> getById(@PathVariable Long id) {
        return exerciseRepo.findById(id)
                .map(e -> ResponseEntity.ok(WorkoutDTO.ExerciseResponse.from(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/rules")
    public ResponseEntity<WorkoutDTO.ExerciseRulesResponse> getRules(@PathVariable Long id) {
        return exerciseRepo.findById(id).<ResponseEntity<WorkoutDTO.ExerciseRulesResponse>>map(e -> {
            try {
                Object kp  = e.getKeypointRulesJson()   != null ? objectMapper.readValue(e.getKeypointRulesJson(),   Object.class) : null;
                Object ang = e.getAngleThresholdsJson()  != null ? objectMapper.readValue(e.getAngleThresholdsJson(), Object.class) : null;
                Object cue = e.getFeedbackCuesJson()     != null ? objectMapper.readValue(e.getFeedbackCuesJson(),    Object.class) : null;
                return ResponseEntity.ok(new WorkoutDTO.ExerciseRulesResponse(
                        id.toString(), e.getName(), kp, ang, cue));
            } catch (JsonProcessingException ex) {
                return ResponseEntity.internalServerError().build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO.ExerciseResponse> create(@RequestBody Map<String, Object> body) {
        Exercise ex = Exercise.builder()
                .name((String) body.get("name"))
                .category((String) body.get("category"))
                .muscleGroup((String) body.get("muscleGroup"))
                .description((String) body.get("description"))
                .difficulty((String) body.getOrDefault("difficulty", "Beginner"))
                .active(true)
                .build();
        if (body.containsKey("keypointRulesJson"))
            ex.setKeypointRulesJson(body.get("keypointRulesJson").toString());
        if (body.containsKey("angleThresholdsJson"))
            ex.setAngleThresholdsJson(body.get("angleThresholdsJson").toString());
        exerciseRepo.save(ex);
        return ResponseEntity.ok(WorkoutDTO.ExerciseResponse.from(ex));
    }
}
