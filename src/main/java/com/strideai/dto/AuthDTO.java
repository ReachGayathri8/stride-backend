package com.strideai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDTO {

    @Getter @Setter
    public static class RegisterRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @Size(min = 8) private String password;
        private Integer age;
        private Double weightKg;
        private Double heightCm;
        private String fitnessGoal = "GENERAL";
        private String budgetTier  = "MEDIUM";
        private String dietPref    = "NONE";
        private boolean male = true;
    }

    @Getter @Setter
    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private UserDTO user;
    }
}
