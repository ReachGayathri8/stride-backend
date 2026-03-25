package com.strideai.config;

import com.strideai.model.Exercise;
import com.strideai.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ExerciseRepository exerciseRepo;

    @Override
    public void run(String... args) {
        if (exerciseRepo.count() > 0) return;

        List<Exercise> exercises = new ArrayList<>();

        exercises.add(build("Squat", "Strength", "Legs", "Beginner",
            "Classic lower-body compound movement targeting quads, glutes and hamstrings.",
            "{\"rep_counter\":{\"joint\":[\"left_hip\",\"left_knee\",\"left_ankle\"],\"lower_threshold\":95,\"upper_threshold\":160}}",
            "[{\"joint\":[\"left_hip\",\"left_knee\",\"left_ankle\"],\"lower\":80,\"upper\":110,\"weight\":0.40,\"cue\":\"Go deeper - aim for 90 degrees at the knee\"},"
            + "{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower\":60,\"upper\":100,\"weight\":0.35,\"cue\":\"Keep your torso more upright\"},"
            + "{\"joint\":[\"right_hip\",\"right_knee\",\"right_ankle\"],\"lower\":80,\"upper\":110,\"weight\":0.25,\"cue\":\"Keep knees tracking over toes\"}]",
            "{\"good\":\"Great squat depth!\",\"ok\":\"A bit deeper please\",\"poor\":\"Slow down and check your form\"}"));

        exercises.add(build("Push Up", "Strength", "Chest", "Beginner",
            "Upper-body push exercise targeting chest, shoulders and triceps.",
            "{\"rep_counter\":{\"joint\":[\"left_shoulder\",\"left_elbow\",\"left_wrist\"],\"lower_threshold\":80,\"upper_threshold\":155}}",
            "[{\"joint\":[\"left_shoulder\",\"left_elbow\",\"left_wrist\"],\"lower\":70,\"upper\":95,\"weight\":0.45,\"cue\":\"Go lower - elbows to 90 degrees\"},"
            + "{\"joint\":[\"left_hip\",\"left_shoulder\",\"left_elbow\"],\"lower\":160,\"upper\":180,\"weight\":0.35,\"cue\":\"Keep body straight - no sagging hips\"},"
            + "{\"joint\":[\"right_shoulder\",\"right_elbow\",\"right_wrist\"],\"lower\":70,\"upper\":95,\"weight\":0.20,\"cue\":\"Keep both arms symmetric\"}]",
            "{\"good\":\"Perfect push-up form!\",\"ok\":\"Keep your body rigid\",\"poor\":\"Reset and go slower\"}"));

        exercises.add(build("Bicep Curl", "Strength", "Arms", "Beginner",
            "Isolation curl for the biceps brachii. Keep elbows pinned to your sides.",
            "{\"rep_counter\":{\"joint\":[\"left_shoulder\",\"left_elbow\",\"left_wrist\"],\"lower_threshold\":50,\"upper_threshold\":145}}",
            "[{\"joint\":[\"left_shoulder\",\"left_elbow\",\"left_wrist\"],\"lower\":30,\"upper\":60,\"weight\":0.50,\"cue\":\"Curl higher - squeeze at the top\"},"
            + "{\"joint\":[\"left_hip\",\"left_shoulder\",\"left_elbow\"],\"lower\":0,\"upper\":25,\"weight\":0.50,\"cue\":\"Keep elbow pinned - do not swing\"}]",
            "{\"good\":\"Great curl! Full range of motion\",\"ok\":\"Full range - all the way up\",\"poor\":\"Control the movement\"}"));

        exercises.add(build("Lunge", "Strength", "Legs", "Intermediate",
            "Unilateral leg exercise improving balance, stability and glute activation.",
            "{\"rep_counter\":{\"joint\":[\"left_hip\",\"left_knee\",\"left_ankle\"],\"lower_threshold\":95,\"upper_threshold\":160}}",
            "[{\"joint\":[\"left_hip\",\"left_knee\",\"left_ankle\"],\"lower\":80,\"upper\":100,\"weight\":0.40,\"cue\":\"Front knee to 90 degrees - step further forward\"},"
            + "{\"joint\":[\"right_hip\",\"right_knee\",\"right_ankle\"],\"lower\":80,\"upper\":100,\"weight\":0.35,\"cue\":\"Back knee should also reach 90 degrees\"},"
            + "{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower\":80,\"upper\":100,\"weight\":0.25,\"cue\":\"Keep your torso upright\"}]",
            "{\"good\":\"Perfect lunge depth!\",\"ok\":\"A bit deeper on both legs\",\"poor\":\"Check your balance and depth\"}"));

        exercises.add(build("Shoulder Press", "Strength", "Shoulders", "Intermediate",
            "Overhead press targeting deltoids and upper trapezius.",
            "{\"rep_counter\":{\"joint\":[\"left_elbow\",\"left_shoulder\",\"left_hip\"],\"lower_threshold\":80,\"upper_threshold\":160}}",
            "[{\"joint\":[\"left_elbow\",\"left_shoulder\",\"left_hip\"],\"lower\":155,\"upper\":180,\"weight\":0.50,\"cue\":\"Press all the way up - full extension\"},"
            + "{\"joint\":[\"right_elbow\",\"right_shoulder\",\"right_hip\"],\"lower\":155,\"upper\":180,\"weight\":0.50,\"cue\":\"Keep both arms even\"}]",
            "{\"good\":\"Full overhead extension!\",\"ok\":\"Press higher\",\"poor\":\"Core tight, do not arch lower back\"}"));

        exercises.add(build("Plank", "Core", "Core", "Beginner",
            "Isometric hold building core stability, shoulder endurance and spinal alignment.",
            "{\"rep_counter\":{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_ankle\"],\"lower_threshold\":150,\"upper_threshold\":180}}",
            "[{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_ankle\"],\"lower\":160,\"upper\":180,\"weight\":1.0,\"cue\":\"Raise your hips - keep body in a straight line\"}]",
            "{\"good\":\"Perfect plank position!\",\"ok\":\"Tighten your core\",\"poor\":\"Check your hip position\"}"));

        exercises.add(build("Jumping Jack", "Cardio", "Full Body", "Beginner",
            "Cardio warm-up raising heart rate and improving coordination.",
            "{\"rep_counter\":{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower_threshold\":90,\"upper_threshold\":160}}",
            "[{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower\":100,\"upper\":170,\"weight\":1.0,\"cue\":\"Full range - arms overhead and legs wide\"}]",
            "{\"good\":\"Great form!\",\"ok\":\"Full range of motion\",\"poor\":\"Arms up and legs wider\"}"));

        exercises.add(build("Deadlift", "Strength", "Back", "Advanced",
            "Hip-hinge movement targeting posterior chain: glutes, hamstrings and back.",
            "{\"rep_counter\":{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower_threshold\":70,\"upper_threshold\":155}}",
            "[{\"joint\":[\"left_shoulder\",\"left_hip\",\"left_knee\"],\"lower\":45,\"upper\":75,\"weight\":0.40,\"cue\":\"Hinge deeper at the hips\"},"
            + "{\"joint\":[\"left_hip\",\"left_shoulder\",\"left_elbow\"],\"lower\":170,\"upper\":180,\"weight\":0.35,\"cue\":\"Keep back flat - do not round\"},"
            + "{\"joint\":[\"left_hip\",\"left_knee\",\"left_ankle\"],\"lower\":130,\"upper\":160,\"weight\":0.25,\"cue\":\"Knees should track over toes\"}]",
            "{\"good\":\"Excellent deadlift form!\",\"ok\":\"Focus on back position\",\"poor\":\"Stop and check your form before continuing\"}"));

        exerciseRepo.saveAll(exercises);
        System.out.println("Seeded " + exercises.size() + " exercises successfully.");
    }

    private Exercise build(String name, String cat, String muscle, String diff,
                           String desc, String kpJson, String anglesJson, String cuesJson) {
        return Exercise.builder()
                .name(name)
                .category(cat)
                .muscleGroup(muscle)
                .difficulty(diff)
                .description(desc)
                .keypointRulesJson(kpJson)
                .angleThresholdsJson(anglesJson)
                .feedbackCuesJson(cuesJson)
                .active(true)
                .build();
    }
}
