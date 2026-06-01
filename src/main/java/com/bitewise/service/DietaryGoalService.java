package com.bitewise.service;

import com.bitewise.domain.DietaryGoal;
import com.bitewise.domain.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.DietaryGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DietaryGoalService {

    private final DietaryGoalRepository dietaryGoalRepository;
    private final UserService userService;

    public DietaryGoalService(DietaryGoalRepository dietaryGoalRepository, UserService userService) {
        this.dietaryGoalRepository = dietaryGoalRepository;
        this.userService = userService;
    }

    public DietaryGoal getForUser(Long userId) {
        return dietaryGoalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizatorul nu are un obiectiv setat"));
    }

    /** Use case: "Seteaza obiective nutritionale". Creeaza sau actualizeaza (0..1). */
    public DietaryGoal setForUser(Long userId, DietaryGoal data) {
        User user = userService.findById(userId);
        DietaryGoal goal = dietaryGoalRepository.findByUserId(userId).orElseGet(DietaryGoal::new);
        goal.setUser(user);
        goal.setTargetCalories(data.getTargetCalories());
        goal.setTargetProtein(data.getTargetProtein());
        goal.setTargetCarbs(data.getTargetCarbs());
        goal.setTargetFats(data.getTargetFats());
        goal.setTargetSugar(data.getTargetSugar());
        goal.setGoalType(data.getGoalType());
        return dietaryGoalRepository.save(goal);
    }
}
