package com.bitewise.service;

import com.bitewise.domain.*;
import com.bitewise.dto.CalorieProgress;
import com.bitewise.dto.MealLogRequest;
import com.bitewise.dto.NutritionSummary;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.DietaryGoalRepository;
import com.bitewise.repository.MealLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final UserService userService;
    private final IngredientService ingredientService;
    private final PantryService pantryService;
    private final DietaryGoalRepository dietaryGoalRepository;

    public MealLogService(MealLogRepository mealLogRepository,
                          UserService userService,
                          IngredientService ingredientService,
                          PantryService pantryService,
                          DietaryGoalRepository dietaryGoalRepository) {
        this.mealLogRepository = mealLogRepository;
        this.userService = userService;
        this.ingredientService = ingredientService;
        this.pantryService = pantryService;
        this.dietaryGoalRepository = dietaryGoalRepository;
    }

    public List<MealLog> forUser(Long userId) {
        return mealLogRepository.findByUserId(userId);
    }

    public List<MealLog> forUserOnDate(Long userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return mealLogRepository.findByUserIdAndConsumedAtBetween(userId, start, end);
    }

    /**
     * Use case: "Inregistreaza jurnal alimentar".
     * «includes» (scade cantitatea): la inregistrare scadem stocul din camara.
     */
    public MealLog log(Long userId, MealLogRequest req) {
        User user = userService.findById(userId);
        Ingredient ingredient = ingredientService.findById(req.getIngredientId());
        LocalDateTime when = req.getConsumedAt() != null ? req.getConsumedAt() : LocalDateTime.now();

        MealLog mealLog = new MealLog(req.getAmountConsumed(), req.getUnit(), when, user, ingredient);
        MealLog saved = mealLogRepository.save(mealLog);

        // includes: scade cantitatea din camara
        pantryService.consumeFromPantry(userId, ingredient.getId(), req.getAmountConsumed());

        return saved;
    }

    public void delete(Long id) {
        MealLog log = mealLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intrarea de jurnal cu id " + id + " nu exista"));
        mealLogRepository.delete(log);
    }

    /** Use case: "Vizualizeaza progres calorii" pentru o zi data. */
    public CalorieProgress dailyProgress(Long userId, LocalDate date) {
        List<MealLog> logs = forUserOnDate(userId, date);

        NutritionSummary consumed = new NutritionSummary();
        for (MealLog log : logs) {
            consumed.add(ingredientService.nutritionFor(log.getIngredient(), log.getAmountConsumed()));
        }

        double target = dietaryGoalRepository.findByUserId(userId)
                .map(DietaryGoal::getTargetCalories)
                .orElse(0.0);

        CalorieProgress progress = new CalorieProgress();
        progress.setConsumed(consumed);
        progress.setConsumedCalories(round(consumed.getCalories()));
        progress.setTargetCalories(target != 0.0 ? target : 0.0);
        progress.setRemaining(round(target - consumed.getCalories()));
        progress.setPercentage(target > 0 ? round(consumed.getCalories() / target * 100.0) : 0.0);
        return progress;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
