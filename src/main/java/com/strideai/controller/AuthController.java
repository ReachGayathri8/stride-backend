package com.strideai.controller;

import com.strideai.dto.AuthDTO;
import com.strideai.dto.UserDTO;
import com.strideai.model.User;
import com.strideai.repository.UserRepository;
import com.strideai.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO.RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }

        User.FitnessGoal fitnessGoal = parseEnum(User.FitnessGoal.class, req.getFitnessGoal(), User.FitnessGoal.GENERAL);
        User.BudgetTier  budgetTier  = parseEnum(User.BudgetTier.class,  req.getBudgetTier(),  User.BudgetTier.MEDIUM);
        User.DietPref    dietPref    = parseEnum(User.DietPref.class,    req.getDietPref(),    User.DietPref.NONE);

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(encoder.encode(req.getPassword()))
                .age(req.getAge())
                .weightKg(req.getWeightKg())
                .heightCm(req.getHeightCm())
                .fitnessGoal(fitnessGoal)
                .budgetTier(budgetTier)
                .dietPref(dietPref)
                .male(req.isMale())
                .build();

        userRepo.save(user);
        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.ok(AuthDTO.AuthResponse.builder()
                .token(token).user(UserDTO.from(user)).build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO.LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            User user = userRepo.findByEmail(req.getEmail()).orElseThrow();
            String token = jwtUtils.generateToken(user.getEmail());
            return ResponseEntity.ok(AuthDTO.AuthResponse.builder()
                    .token(token).user(UserDTO.from(user)).build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    private <T extends Enum<T>> T parseEnum(Class<T> cls, String val, T defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try { return Enum.valueOf(cls, val.toUpperCase()); }
        catch (IllegalArgumentException e) { return defaultVal; }
    }
}
