package com.strideai.controller;

import com.strideai.model.User;
import com.strideai.model.WorkoutSession;
import com.strideai.repository.UserRepository;
import com.strideai.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired private WorkoutSessionRepository sessionRepo;
    @Autowired private UserRepository userRepo;

    private User getUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        long totalSessions = sessionRepo.countByUser(user);
        long totalReps     = sessionRepo.sumTotalRepsByUser(user);
        Double avgAcc      = sessionRepo.avgAccuracyByUser(user);

        List<WorkoutSession> recent = sessionRepo.findTop7ByUserOrderByStartedAtDesc(user);
        Collections.reverse(recent);

        List<Map<String, Object>> accuracyTrend = recent.stream().map(s -> {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("date", s.getStartedAt() != null
                    ? s.getStartedAt().format(DateTimeFormatter.ofPattern("EEE")) : "N/A");
            p.put("accuracy", s.getAvgAccuracy() != null ? s.getAvgAccuracy().intValue() : 0);
            return p;
        }).collect(Collectors.toList());

        int streak = calculateStreak(recent);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSessions", totalSessions);
        result.put("totalReps",     totalReps);
        result.put("avgAccuracy",   avgAcc != null ? Math.round(avgAcc) : 0);
        result.put("streak",        streak);
        result.put("accuracyTrend", accuracyTrend);
        result.put("volumeTrend",   buildVolumeTrend(recent));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress(@AuthenticationPrincipal UserDetails ud) {
        return getDashboard(ud);
    }

    @GetMapping("/consistency")
    public ResponseEntity<Map<String, Object>> getConsistency(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        List<WorkoutSession> recent = sessionRepo.findTop7ByUserOrderByStartedAtDesc(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("streak",        calculateStreak(recent));
        result.put("totalSessions", sessionRepo.countByUser(user));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformance(@AuthenticationPrincipal UserDetails ud) {
        User user = getUser(ud);
        Double avg = sessionRepo.avgAccuracyByUser(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("avgAccuracy", avg != null ? Math.round(avg) : 0);
        return ResponseEntity.ok(result);
    }

    private int calculateStreak(List<WorkoutSession> sessions) {
        if (sessions.isEmpty()) return 0;
        int streak = 1;
        for (int i = 0; i < sessions.size() - 1; i++) {
            LocalDateTime a = sessions.get(i).getStartedAt();
            LocalDateTime b = sessions.get(i + 1).getStartedAt();
            if (a != null && b != null &&
                    a.toLocalDate().minusDays(1).equals(b.toLocalDate())) {
                streak++;
            } else break;
        }
        return streak;
    }

    private List<Map<String, Object>> buildVolumeTrend(List<WorkoutSession> sessions) {
        List<Map<String, Object>> result = new ArrayList<>();
        int count = Math.min(sessions.size(), 4);
        for (int i = 0; i < count; i++) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("week", "W" + (i + 1));
            Integer reps = sessions.get(i).getTotalReps();
            m.put("reps", reps != null ? reps : 0);
            result.add(m);
        }
        return result;
    }
}
