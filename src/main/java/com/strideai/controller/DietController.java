package com.strideai.controller;

import com.strideai.model.User;
import com.strideai.repository.UserRepository;
import com.strideai.service.DietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/diet")
public class DietController {

    @Autowired private DietService dietService;
    @Autowired private UserRepository userRepo;

    private User getUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrent(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(dietService.getCurrentPlan(getUser(ud)));
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(dietService.generateAndSave(getUser(ud)));
    }
}
