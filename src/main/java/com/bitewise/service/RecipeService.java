package com.bitewise.service;

import com.bitewise.domain.*;
import com.bitewise.dto.NutritionSummary;
import com.bitewise.dto.RecipeRequest;
import com.bitewise.dto.ShoppingListItem;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final IngredientService ingredientService;
    private final PantryService pantryService;

    public RecipeService(RecipeRepository recipeRepository,
                         UserService userService,
                         IngredientService ingredientService,
                         PantryService pantryService) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.ingredientService = ingredientService;
        this.pantryService = pantryService;
    }

    public List<Recipe> forUser(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reteta cu id " + id + " nu exista"));
    }

    /** Use case: "Creeaza reteta noua". */
    public Recipe create(Long userId, RecipeRequest req) {
        User user = userService.findById(userId);
        Recipe recipe = new Recipe(req.getTitle(), req.getInstructions(), user);
        applyIngredients(recipe, req);
        return recipeRepository.save(recipe);
    }

    public Recipe update(Long id, RecipeRequest req) {
        Recipe recipe = findById(id);
        recipe.setTitle(req.getTitle());
        recipe.setInstructions(req.getInstructions());
        recipe.getIngredients().clear();
        applyIngredients(recipe, req);
        return recipeRepository.save(recipe);
    }

    public void delete(Long id) {
        recipeRepository.delete(findById(id));
    }

    private void applyIngredients(Recipe recipe, RecipeRequest req) {
        if (req.getIngredients() == null) return;
        for (RecipeRequest.Line line : req.getIngredients()) {
            Ingredient ingredient = ingredientService.findById(line.getIngredientId());
            RecipeIngredient ri = new RecipeIngredient(line.getQuantity(), line.getUnit(), recipe, ingredient);
            recipe.getIngredients().add(ri);
        }
    }

    /** Use case: "Calculeaza calorii reteta" — totalul nutritional al retetei. */
    public NutritionSummary computeNutrition(Long recipeId) {
        Recipe recipe = findById(recipeId);
        NutritionSummary total = new NutritionSummary();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            // tratam cantitatea ca grame/ml pentru raportare la valorile per 100
            total.add(ingredientService.nutritionFor(ri.getIngredient(), ri.getQuantity()));
        }
        return total;
    }

    /**
     * Genereaza lista de cumparaturi pentru o reteta, scazand ce exista deja in camara.
     * (functionalitate MealPlanner: liste de cumparaturi)
     */
    public List<ShoppingListItem> shoppingList(Long recipeId, Long userId) {
        Recipe recipe = findById(recipeId);
        List<ShoppingListItem> list = new ArrayList<>();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            double needed = ri.getQuantity();
            double inPantry = pantryService.availableQuantity(userId, ri.getIngredient().getId());
            double toBuy = Math.max(0, needed - inPantry);
            list.add(new ShoppingListItem(
                    ri.getIngredient().getName(),
                    needed,
                    inPantry,
                    toBuy,
                    ri.getUnit()
            ));
        }
        return list;
    }
}
