package com.strideai.controller;

import com.strideai.model.Exercise;
import com.strideai.model.User;
import com.strideai.repository.ExerciseRepository;
import com.strideai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    @Autowired private UserRepository userRepo;
    @Autowired private ExerciseRepository exerciseRepo;

    private User getUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername()).orElseThrow();
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        List<Exercise> exercises = exerciseRepo.findByActiveTrue();
        String goal = (user.getFitnessGoal() != null) ? user.getFitnessGoal().name() : "GENERAL";

        String[][] days;
        if ("BULK".equals(goal)) {
            days = new String[][]{
                {"Monday","Chest + Triceps"},{"Tuesday","Back + Biceps"},
                {"Wednesday","Rest"},{"Thursday","Legs"},
                {"Friday","Shoulders"},{"Saturday","Full Body"},{"Sunday","Rest"}
            };
        } else if ("CUT".equals(goal)) {
            days = new String[][]{
                {"Monday","Cardio + Core"},{"Tuesday","Upper Body"},
                {"Wednesday","Cardio"},{"Thursday","Lower Body"},
                {"Friday","HIIT"},{"Saturday","Active Recovery"},{"Sunday","Rest"}
            };
        } else {
            days = new String[][]{
                {"Monday","Full Body A"},{"Tuesday","Rest"},
                {"Wednesday","Full Body B"},{"Thursday","Rest"},
                {"Friday","Full Body C"},{"Saturday","Cardio"},{"Sunday","Rest"}
            };
        }

        List<Map<String, Object>> schedule = new ArrayList<>();
        for (String[] day : days) {
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("day",    day[0]);
            d.put("focus",  day[1]);
            boolean isRest = "Rest".equals(day[1]);
            d.put("isRest", isRest);
            if (!isRest && !exercises.isEmpty()) {
                int count = Math.min(4, exercises.size());
                List<Map<String, Object>> exList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Exercise e = exercises.get(i);
                    Map<String, Object> em = new LinkedHashMap<>();
                    em.put("id",   e.getId());
                    em.put("name", e.getName());
                    em.put("sets", 3);
                    em.put("reps", 12);
                    exList.add(em);
                }
                d.put("exercises", exList);
            } else {
                d.put("exercises", new ArrayList<>());
            }
            schedule.add(d);
        }

        Map<String, Object> plan = new LinkedHashMap<>();
        plan.put("name",        goal + " Training Plan");
        plan.put("goal",        goal);
        plan.put("schedule",    schedule);
        plan.put("generatedAt", java.time.LocalDate.now().toString());
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrent(@AuthenticationPrincipal UserDetails ud) {
        return generate(ud);
    }
}
