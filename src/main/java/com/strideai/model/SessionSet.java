package com.strideai.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = "session_sets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionSet {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private Integer repsCompleted;
    private Double  accuracyScore;

    @Column(columnDefinition = "TEXT")
    private String feedbackLog; // JSON array of feedback strings

    private Integer durationSec;
}
