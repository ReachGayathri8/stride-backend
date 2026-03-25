package com.strideai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strideai.model.DietPlan;
import com.strideai.model.User;
import com.strideai.repository.DietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DietService {

    @Autowired private DietPlanRepository dietPlanRepo;
    @Autowired private ObjectMapper objectMapper;

    public Map<String, Object> getCurrentPlan(User user) {
        return dietPlanRepo.findTopByUserOrderByValidFromDesc(user)
                .map(this::planToMap)
                .orElseGet(() -> generatePlanMap(user));
    }

    public Map<String, Object> generateAndSave(User user) {
        Map<String, Object> planMap = generatePlanMap(user);
        try {
            DietPlan plan = DietPlan.builder()
                    .user(user)
                    .goal(user.getFitnessGoal() != null ? user.getFitnessGoal().name() : "GENERAL")
                    .caloriesTarget(toInt(planMap.get("caloriesTarget")))
                    .proteinG(toDouble(planMap.get("proteinG")))
                    .carbsG(toDouble(planMap.get("carbsG")))
                    .fatG(toDouble(planMap.get("fatG")))
                    .mealScheduleJson(objectMapper.writeValueAsString(planMap.get("mealSchedule")))
                    .validFrom(LocalDate.now())
                    .build();
            dietPlanRepo.save(plan);
        } catch (Exception ignored) {}
        return planMap;
    }

    private Map<String, Object> generatePlanMap(User user) {
        int bmr  = calcBMR(user);
        int tdee = (int)(bmr * 1.55);

        User.FitnessGoal goal = (user.getFitnessGoal() != null)
                ? user.getFitnessGoal() : User.FitnessGoal.GENERAL;

        int target;
        if      (goal == User.FitnessGoal.BULK)   target = tdee + 300;
        else if (goal == User.FitnessGoal.CUT)    target = tdee - 400;
        else                                       target = tdee;

        double wt      = (user.getWeightKg() != null) ? user.getWeightKg() : 70.0;
        double protein = wt * 2.0;
        double fat     = Math.round((target * 0.25) / 9.0 * 10) / 10.0;
        double carbs   = Math.round(((target - protein * 4 - fat * 9) / 4.0) * 10) / 10.0;

        List<Map<String, Object>> meals = buildMeals(user, target, protein);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("goal",           goal.name());
        result.put("caloriesTarget", target);
        result.put("proteinG",       Math.round(protein));
        result.put("carbsG",         Math.round(carbs));
        result.put("fatG",           Math.round(fat));
        result.put("mealSchedule",   meals);
        return result;
    }

    private List<Map<String, Object>> buildMeals(User user, int totalCal, double totalProtein) {
        boolean isVeg = user.getDietPref() == User.DietPref.VEG
                     || user.getDietPref() == User.DietPref.VEGAN;
        boolean isBulk     = user.getFitnessGoal() == User.FitnessGoal.BULK;
        boolean highBudget = user.getBudgetTier()  == User.BudgetTier.HIGH;

        List<Map<String, Object>> meals = new ArrayList<>();

        meals.add(meal("Breakfast",
                isVeg ? Arrays.asList("Oats with banana (60g)", "Whole milk 200ml", "Mixed nuts 20g")
                      : Arrays.asList("Oats with banana (60g)", "Scrambled eggs x2", "Greek yogurt 100g"),
                (int)(totalCal * 0.25), (int)(totalProtein * 0.20)));

        meals.add(meal("Lunch",
                isVeg ? Arrays.asList("Paneer 100g", "Brown rice 120g", "Dal fry", "Mixed vegetables")
                      : highBudget
                          ? Arrays.asList("Grilled chicken breast 180g", "Quinoa 100g", "Steamed broccoli", "Olive oil drizzle")
                          : Arrays.asList("Chicken thigh 150g", "Brown rice 100g", "Salad"),
                (int)(totalCal * 0.35), (int)(totalProtein * 0.35)));

        meals.add(meal("Snack",
                isVeg ? Arrays.asList("Peanut butter 30g", "Apple", "Roasted chickpeas 30g")
                      : Arrays.asList("Protein shake", "Boiled egg x1", "Almonds 20g"),
                (int)(totalCal * 0.15), (int)(totalProtein * 0.15)));

        meals.add(meal("Dinner",
                isVeg ? Arrays.asList("Tofu stir-fry 150g", "Sweet potato 150g", "Steamed greens")
                      : Arrays.asList("Baked salmon 130g", "Sweet potato 150g", "Mixed salad"),
                (int)(totalCal * 0.20), (int)(totalProtein * 0.25)));

        if (isBulk) {
            meals.add(meal("Pre-bed Snack",
                    isVeg ? Arrays.asList("Cottage cheese 150g", "Walnuts 20g")
                          : Arrays.asList("Casein protein shake", "Peanut butter 1 tbsp"),
                    (int)(totalCal * 0.05), (int)(totalProtein * 0.05)));
        }
        return meals;
    }

    private Map<String, Object> meal(String name, List<String> items, int cal, int protein) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("meal",     name);
        m.put("items",    items);
        m.put("calories", cal);
        m.put("protein",  protein);
        return m;
    }

    private int calcBMR(User user) {
        double w = (user.getWeightKg() != null) ? user.getWeightKg() : 70.0;
        double h = (user.getHeightCm() != null) ? user.getHeightCm() : 170.0;
        int    a = (user.getAge()      != null) ? user.getAge()      : 25;
        return (int)(10 * w + 6.25 * h - 5 * a + (user.isMale() ? 5 : -161));
    }

    private Map<String, Object> planToMap(DietPlan p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("goal",           p.getGoal());
        m.put("caloriesTarget", p.getCaloriesTarget());
        m.put("proteinG",       p.getProteinG());
        m.put("carbsG",         p.getCarbsG());
        m.put("fatG",           p.getFatG());
        try {
            m.put("mealSchedule", objectMapper.readValue(p.getMealScheduleJson(), Object.class));
        } catch (Exception e) {
            m.put("mealSchedule", new ArrayList<>());
        }
        return m;
    }

    private int toInt(Object v) {
        return (v instanceof Number) ? ((Number) v).intValue() : 0;
    }

    private double toDouble(Object v) {
        return (v instanceof Number) ? ((Number) v).doubleValue() : 0.0;
    }
}
