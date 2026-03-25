package com.strideai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    public enum Difficulty { Beginner, Intermediate, Advanced }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;
    private String muscleGroup;
    private String description;

    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String keypointRulesJson;

    @Column(columnDefinition = "TEXT")
    private String angleThresholdsJson;

    @Column(columnDefinition = "TEXT")
    private String feedbackCuesJson;

    private boolean active = true;
}
