package com.strideai.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public enum FitnessGoal { BULK, CUT, RECOMP, GENERAL }
    public enum BudgetTier  { LOW, MEDIUM, HIGH }
    public enum DietPref    { NONE, VEG, VEGAN, NON_VEG }

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
    private Double weightKg;
    private Double heightCm;
    private boolean male = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FitnessGoal fitnessGoal = FitnessGoal.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private BudgetTier budgetTier = BudgetTier.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DietPref dietPref = DietPref.NONE;

    @CreatedDate
    private LocalDateTime createdAt;
}
