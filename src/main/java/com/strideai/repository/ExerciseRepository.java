package com.strideai.repository;

import com.strideai.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByActiveTrue();
    List<Exercise> findByCategoryAndActiveTrue(String category);
    List<Exercise> findByDifficultyAndActiveTrue(String difficulty);
}
