package com.strideai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "diet_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String goal;
    private Integer caloriesTarget;
    private Double proteinG;
    private Double carbsG;
    private Double fatG;

    @Column(columnDefinition = "TEXT")
    private String mealScheduleJson;

    private LocalDate validFrom;
}
