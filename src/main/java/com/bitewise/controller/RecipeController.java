package com.bitewise.controller;

import com.bitewise.domain.Recipe;
import com.bitewise.dto.NutritionSummary;
import com.bitewise.dto.RecipeRequest;
import com.bitewise.dto.ShoppingListItem;
import com.bitewise.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping("/recipes")
    public List<Recipe> all() {
        return service.findAll();
    }

    @GetMapping("/users/{userId}/recipes")
    public List<Recipe> forUser(@PathVariable Long userId) {
        return service.forUser(userId);
    }

    @GetMapping("/recipes/{id}")
    public Recipe one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/users/{userId}/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe create(@PathVariable Long userId, @Valid @RequestBody RecipeRequest req) {
        return service.create(userId, req);
    }

    @PutMapping("/recipes/{id}")
    public Recipe update(@PathVariable Long id, @Valid @RequestBody RecipeRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipes/{id}/nutrition")
    public NutritionSummary nutrition(@PathVariable Long id) {
        return service.computeNutrition(id);
    }

    @GetMapping("/recipes/{id}/shopping-list")
    public List<ShoppingListItem> shoppingList(@PathVariable Long id, @RequestParam Long userId) {
        return service.shoppingList(id, userId);
    }
}
