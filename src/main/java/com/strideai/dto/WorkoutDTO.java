package com.strideai.dto;

import com.strideai.model.Exercise;
import com.strideai.model.WorkoutSession;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class WorkoutDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExerciseResponse {
        private Long id;
        private String name;
        private String category;
        private String muscleGroup;
        private String difficulty;
        private String description;

        public static ExerciseResponse from(Exercise e) {
            return ExerciseResponse.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .category(e.getCategory())
                    .muscleGroup(e.getMuscleGroup())
                    .difficulty(e.getDifficulty())
                    .description(e.getDescription())
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExerciseRulesResponse {
        private String id;
        private String name;
        private Object keypointRules;
        private Object angleThresholds;
        private Object feedbackCues;

        public ExerciseRulesResponse(String id, String name, Object kp, Object ang, Object cues) {
            this.id = id; this.name = name;
            this.keypointRules = kp; this.angleThresholds = ang; this.feedbackCues = cues;
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SessionResponse {
        private UUID id;
        private String exerciseName;
        private String exerciseId;
        private Integer totalReps;
        private Double avgAccuracy;
        private String status;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private long durationMin;

        public static SessionResponse from(WorkoutSession s) {
            long dur = 0;
            if (s.getStartedAt() != null && s.getEndedAt() != null) {
                dur = java.time.Duration.between(s.getStartedAt(), s.getEndedAt()).toMinutes();
            }
            return SessionResponse.builder()
                    .id(s.getId())
                    .exerciseName(s.getExercise() != null ? s.getExercise().getName() : "Unknown")
                    .exerciseId(s.getExercise() != null ? s.getExercise().getId().toString() : null)
                    .totalReps(s.getTotalReps())
                    .avgAccuracy(s.getAvgAccuracy())
                    .status(s.getStatus() != null ? s.getStatus().name() : null)
                    .startedAt(s.getStartedAt())
                    .endedAt(s.getEndedAt())
                    .durationMin(dur)
                    .build();
        }
    }

    @Getter @Setter
    public static class StartSessionRequest {
        private Long exerciseId;
    }

    @Getter @Setter
    public static class CompleteSessionRequest {
        private Integer totalReps;
        private Double avgAccuracy;
        private String status;
    }

    @Getter @Setter
    public static class LogSetRequest {
        private Long exerciseId;
        private Integer repsCompleted;
        private Double accuracyScore;
        private String feedbackLog;
        private Integer durationSec;
    }
}
