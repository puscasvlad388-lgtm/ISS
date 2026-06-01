package com.bitewise.service;

import com.bitewise.domain.Category;
import com.bitewise.domain.Ingredient;
import com.bitewise.dto.NutritionSummary;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.CategoryRepository;
import com.bitewise.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;

    public IngredientService(IngredientRepository ingredientRepository, CategoryRepository categoryRepository) {
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient findById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredientul cu id " + id + " nu exista"));
    }

    public List<Ingredient> search(String name) {
        if (name == null || name.isBlank()) {
            return ingredientRepository.findAll();
        }
        return ingredientRepository.findByNameContainingIgnoreCase(name);
    }

    public Ingredient create(Ingredient ingredient, Long categoryId) {
        attachCategory(ingredient, categoryId);
        return ingredientRepository.save(ingredient);
    }

    public Ingredient update(Long id, Ingredient data, Long categoryId) {
        Ingredient existing = findById(id);
        existing.setName(data.getName());
        existing.setCalories(data.getCalories());
        existing.setProtein(data.getProtein());
        existing.setCarbs(data.getCarbs());
        existing.setSugars(data.getSugars());
        existing.setFats(data.getFats());
        attachCategory(existing, categoryId);
        return ingredientRepository.save(existing);
    }

    public void delete(Long id) {
        ingredientRepository.delete(findById(id));
    }

    private void attachCategory(Ingredient ingredient, Long categoryId) {
        if (categoryId != null) {
            Category c = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria cu id " + categoryId + " nu exista"));
            ingredient.setCategory(c);
        } else {
            ingredient.setCategory(null);
        }
    }

    /**
     * Calculeaza aportul nutritional pentru o anumita cantitate (in grame/ml)
     * dintr-un ingredient. Valorile din ingredient sunt per 100 unitati.
     */
    public NutritionSummary nutritionFor(Ingredient ingredient, double amount) {
        double factor = amount / 100.0;
        return new NutritionSummary(
                ingredient.getCalories() * factor,
                ingredient.getProtein() * factor,
                ingredient.getCarbs() * factor,
                ingredient.getSugars() * factor,
                ingredient.getFats() * factor
        );
    }
}
