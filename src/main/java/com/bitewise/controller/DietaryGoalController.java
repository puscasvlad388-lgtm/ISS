package com.bitewise.controller;

import com.bitewise.domain.DietaryGoal;
import com.bitewise.service.DietaryGoalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/goal")
public class DietaryGoalController {

    private final DietaryGoalService service;

    public DietaryGoalController(DietaryGoalService service) {
        this.service = service;
    }

    @GetMapping
    public DietaryGoal get(@PathVariable Long userId) {
        return service.getForUser(userId);
    }

    @PutMapping
    public DietaryGoal set(@PathVariable Long userId, @RequestBody DietaryGoal goal) {
        return service.setForUser(userId, goal);
    }
}
