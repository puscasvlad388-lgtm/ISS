package com.bitewise.controller;

import com.bitewise.domain.Ingredient;
import com.bitewise.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public List<Ingredient> all(@RequestParam(required = false) String q) {
        return ingredientService.search(q);
    }

    @GetMapping("/{id}")
    public Ingredient one(@PathVariable Long id) { return ingredientService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ingredient create(@Valid @RequestBody Ingredient ingredient,
                             @RequestParam(required = false) Long categoryId) {
        return ingredientService.create(ingredient, categoryId);
    }

    @PutMapping("/{id}")
    public Ingredient update(@PathVariable Long id, @Valid @RequestBody Ingredient ingredient,
                             @RequestParam(required = false) Long categoryId) {
        return ingredientService.update(id, ingredient, categoryId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
