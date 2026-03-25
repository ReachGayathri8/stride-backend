package com.strideai.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    public enum FitnessGoal { BULK, CUT, RECOMP, GENERAL }
    public enum BudgetTier  { LOW, MEDIUM, HIGH }
    public enum DietPref    { NONE, VEG, VEGAN, NON_VEG }
    public enum Sex         { MALE, FEMALE, OTHER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    private Integer age;
    private Double  weightKg;
    private Double  heightCm;

    // Legacy boolean kept for backward compat — use sex enum going forward
    private boolean male = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Sex sex = Sex.MALE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private FitnessGoal fitnessGoal = FitnessGoal.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private BudgetTier budgetTier = BudgetTier.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private DietPref dietPref = DietPref.NONE;

    // Beginner mode flag — auto-set during registration based on age + goal
    @Builder.Default
    private boolean beginnerMode = false;

    @CreatedDate
    private LocalDateTime createdAt;
}