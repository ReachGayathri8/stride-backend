package com.strideai.controller;

import com.strideai.dto.WorkoutDTO;
import com.strideai.model.*;
import com.strideai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
public class WorkoutSessionController {

    @Autowired private WorkoutSessionRepository sessionRepo;
    @Autowired private SessionSetRepository setRepo;
    @Autowired private ExerciseRepository exerciseRepo;
    @Autowired private UserRepository userRepo;

    private User getUser(UserDetails ud) {
        return userRepo.findByEmail(ud.getUsername()).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO.SessionResponse> startSession(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody WorkoutDTO.StartSessionRequest req) {
        User user = getUser(ud);
        Exercise exercise = (req.getExerciseId() != null)
                ? exerciseRepo.findById(req.getExerciseId()).orElse(null) : null;

        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .exercise(exercise)
                .status(WorkoutSession.Status.IN_PROGRESS)
                .build();
        sessionRepo.save(session);
        return ResponseEntity.ok(WorkoutDTO.SessionResponse.from(session));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<WorkoutDTO.SessionResponse> completeSession(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable UUID id,
            @RequestBody WorkoutDTO.CompleteSessionRequest req) {
        User user = getUser(ud);
        Optional<WorkoutSession> opt = sessionRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        WorkoutSession s = opt.get();
        if (!s.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        s.setTotalReps(req.getTotalReps());
        s.setAvgAccuracy(req.getAvgAccuracy());
        s.setEndedAt(LocalDateTime.now());
        try {
            s.setStatus(WorkoutSession.Status.valueOf(req.getStatus()));
        } catch (Exception e) {
            s.setStatus(WorkoutSession.Status.COMPLETED);
        }
        sessionRepo.save(s);
        return ResponseEntity.ok(WorkoutDTO.SessionResponse.from(s));
    }

    @PostMapping("/{id}/sets")
    public ResponseEntity<Map<String, Object>> logSet(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable UUID id,
            @RequestBody WorkoutDTO.LogSetRequest req) {
        Optional<WorkoutSession> opt = sessionRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        WorkoutSession session = opt.get();
        Exercise ex = (req.getExerciseId() != null)
                ? exerciseRepo.findById(req.getExerciseId()).orElse(session.getExercise())
                : session.getExercise();

        SessionSet set = SessionSet.builder()
                .session(session)
                .exercise(ex)
                .repsCompleted(req.getRepsCompleted())
                .accuracyScore(req.getAccuracyScore())
                .feedbackLog(req.getFeedbackLog())
                .durationSec(req.getDurationSec())
                .build();
        setRepo.save(set);

        Map<String, Object> response = new HashMap<>();
        response.put("id",   set.getId());
        response.put("reps", set.getRepsCompleted());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(ud);
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkoutSession> sessions = sessionRepo.findByUserOrderByStartedAtDesc(user, pageable);

        List<WorkoutDTO.SessionResponse> content = sessions.getContent().stream()
                .map(WorkoutDTO.SessionResponse::from)
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content",       content);
        result.put("totalElements", sessions.getTotalElements());
        result.put("totalPages",    sessions.getTotalPages());
        result.put("page",          page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO.SessionResponse> getById(
            @AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        User user = getUser(ud);
        Optional<WorkoutSession> opt = sessionRepo.findById(id);
        if (opt.isEmpty() || !opt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(WorkoutDTO.SessionResponse.from(opt.get()));
    }
}
