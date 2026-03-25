package com.strideai.dto;

import lombok.*;
import com.strideai.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Double weightKg;
    private Double heightCm;
    private String fitnessGoal;
    private String budgetTier;
    private String dietPref;
    private boolean male;

    public static UserDTO from(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .age(u.getAge())
                .weightKg(u.getWeightKg())
                .heightCm(u.getHeightCm())
                .fitnessGoal(u.getFitnessGoal() != null ? u.getFitnessGoal().name() : "GENERAL")
                .budgetTier(u.getBudgetTier()  != null ? u.getBudgetTier().name()  : "MEDIUM")
                .dietPref(u.getDietPref()      != null ? u.getDietPref().name()    : "NONE")
                .male(u.isMale())
                .build();
    }
}
