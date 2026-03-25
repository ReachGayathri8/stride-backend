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
        if (body.containsKey("name"))
            user.setName((String) body.get("name"));
        if (body.containsKey("age"))
            user.setAge(((Number) body.get("age")).intValue());
        if (body.containsKey("weightKg"))
            user.setWeightKg(((Number) body.get("weightKg")).doubleValue());
        if (body.containsKey("heightCm"))
            user.setHeightCm(((Number) body.get("heightCm")).doubleValue());
        if (body.containsKey("fitnessGoal") && body.get("fitnessGoal") != null) {
            try { user.setFitnessGoal(User.FitnessGoal.valueOf((String) body.get("fitnessGoal"))); }
            catch (IllegalArgumentException ignored) {}
        }
        if (body.containsKey("budgetTier") && body.get("budgetTier") != null) {
            try { user.setBudgetTier(User.BudgetTier.valueOf((String) body.get("budgetTier"))); }
            catch (IllegalArgumentException ignored) {}
        }
        if (body.containsKey("dietPref") && body.get("dietPref") != null) {
            try { user.setDietPref(User.DietPref.valueOf((String) body.get("dietPref"))); }
            catch (IllegalArgumentException ignored) {}
        }
        if (body.containsKey("password") && body.get("password") != null) {
            user.setPasswordHash(encoder.encode((String) body.get("password")));
        }
        userRepo.save(user);
        return ResponseEntity.ok(UserDTO.from(user));
    }
}
