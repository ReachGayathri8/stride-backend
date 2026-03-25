package com.strideai.controller;

import com.strideai.dto.UserDTO;
import com.strideai.model.User;
import com.strideai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    private User getUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(UserDTO.from(getUser(ud)));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal UserDetails ud,
                                      @RequestBody Map<String, Object> body) {
        User user = getUser(ud);

        if (body.containsKey("name") && body.get("name") != null)
            user.setName((String) body.get("name"));

        if (body.containsKey("age") && body.get("age") != null)
            user.setAge(((Number) body.get("age")).intValue());

        if (body.containsKey("weightKg") && body.get("weightKg") != null)
            user.setWeightKg(((Number) body.get("weightKg")).doubleValue());

        if (body.containsKey("heightCm") && body.get("heightCm") != null)
            user.setHeightCm(((Number) body.get("heightCm")).doubleValue());

        if (body.containsKey("fitnessGoal") && body.get("fitnessGoal") != null) {
            try {
                User.FitnessGoal g = User.FitnessGoal.valueOf((String) body.get("fitnessGoal"));
                user.setFitnessGoal(g);
                // Re-evaluate beginner mode when goal changes
                user.setBeginnerMode(g == User.FitnessGoal.GENERAL || (user.getAge() != null && user.getAge() < 20));
            } catch (IllegalArgumentException ignored) {}
        }

        if (body.containsKey("budgetTier") && body.get("budgetTier") != null) {
            try { user.setBudgetTier(User.BudgetTier.valueOf((String) body.get("budgetTier"))); }
            catch (IllegalArgumentException ignored) {}
        }

        if (body.containsKey("dietPref") && body.get("dietPref") != null) {
            try { user.setDietPref(User.DietPref.valueOf((String) body.get("dietPref"))); }
            catch (IllegalArgumentException ignored) {}
        }

        if (body.containsKey("sex") && body.get("sex") != null) {
            try {
                User.Sex s = User.Sex.valueOf((String) body.get("sex"));
                user.setSex(s);
                user.setMale(s == User.Sex.MALE);
            } catch (IllegalArgumentException ignored) {}
        }

        if (body.containsKey("password") && body.get("password") != null) {
            String pw = (String) body.get("password");
            if (!pw.isBlank()) user.setPasswordHash(encoder.encode(pw));
        }

        userRepo.save(user);
        return ResponseEntity.ok(UserDTO.from(user));
    }
}