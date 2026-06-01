package com.bitewise.config;

import com.bitewise.domain.*;
import com.bitewise.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Populeaza baza de date in-memory cu date de exemplu la fiecare pornire,
 * astfel incat aplicatia sa fie functionala imediat la demonstratie.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final PantryItemRepository pantryItemRepository;
    private final MealLogRepository mealLogRepository;
    private final DietaryGoalRepository dietaryGoalRepository;
    private final WeightTrackerRepository weightTrackerRepository;

    public DataSeeder(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      IngredientRepository ingredientRepository,
                      RecipeRepository recipeRepository,
                      PantryItemRepository pantryItemRepository,
                      MealLogRepository mealLogRepository,
                      DietaryGoalRepository dietaryGoalRepository,
                      WeightTrackerRepository weightTrackerRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.pantryItemRepository = pantryItemRepository;
        this.mealLogRepository = mealLogRepository;
        this.dietaryGoalRepository = dietaryGoalRepository;
        this.weightTrackerRepository = weightTrackerRepository;
    }

    @Override
    public void run(String... args) {
        // --- Categorii ---
        Category lactate = categoryRepository.save(new Category("Lactate"));
        Category carne = categoryRepository.save(new Category("Carne si peste"));
        Category legume = categoryRepository.save(new Category("Legume"));
        Category fructe = categoryRepository.save(new Category("Fructe"));
        Category cereale = categoryRepository.save(new Category("Cereale si paste"));

        // --- Ingrediente (valori per 100g) ---
        Ingredient oua = ing("Oua", 155, 13, 1.1, 1.1, 11, carne);
        Ingredient pieptPui = ing("Piept de pui", 165, 31, 0, 0, 3.6, carne);
        Ingredient orez = ing("Orez fiert", 130, 2.7, 28, 0.1, 0.3, cereale);
        Ingredient brocoli = ing("Brocoli", 34, 2.8, 7, 1.7, 0.4, legume);
        Ingredient rosii = ing("Rosii", 18, 0.9, 3.9, 2.6, 0.2, legume);
        Ingredient branza = ing("Branza telemea", 264, 14, 4, 1, 21, lactate);
        Ingredient iaurt = ing("Iaurt grecesc", 59, 10, 3.6, 3.2, 0.4, lactate);
        Ingredient banana = ing("Banana", 89, 1.1, 23, 12, 0.3, fructe);
        Ingredient ovaz = ing("Fulgi de ovaz", 389, 16.9, 66, 0.99, 6.9, cereale);
        Ingredient ulei = ing("Ulei de masline", 884, 0, 0, 0, 100, legume);

        List<Ingredient> all = List.of(oua, pieptPui, orez, brocoli, rosii, branza, iaurt, banana, ovaz, ulei);
        ingredientRepository.saveAll(all);

        // --- Utilizator demo ---
        User user = new User("Andrei Pop", "andrei@bitewise.ro", 78.0, 180.0, 24, "male");
        userRepository.save(user);

        // --- Obiectiv nutritional ---
        DietaryGoal goal = new DietaryGoal();
        goal.setUser(user);
        goal.setTargetCalories(2200.0);
        goal.setTargetProtein(140.0);
        goal.setTargetCarbs(220.0);
        goal.setTargetFats(70.0);
        goal.setTargetSugar(50.0);
        goal.setGoalType("maintain");
        dietaryGoalRepository.save(goal);

        // --- Istoric greutate ---
        weightTrackerRepository.save(new WeightTracker(82.0, LocalDate.now().minusDays(30), user));
        weightTrackerRepository.save(new WeightTracker(80.5, LocalDate.now().minusDays(20), user));
        weightTrackerRepository.save(new WeightTracker(79.0, LocalDate.now().minusDays(10), user));
        weightTrackerRepository.save(new WeightTracker(78.0, LocalDate.now(), user));

        // --- Camara ---
        pantryItemRepository.save(new PantryItem(500.0, LocalDate.now().plusDays(3), user, pieptPui));
        pantryItemRepository.save(new PantryItem(1000.0, LocalDate.now().plusDays(40), user, orez));
        pantryItemRepository.save(new PantryItem(300.0, LocalDate.now().plusDays(2), user, brocoli));
        pantryItemRepository.save(new PantryItem(12.0, LocalDate.now().plusDays(15), user, oua));
        pantryItemRepository.save(new PantryItem(400.0, LocalDate.now().plusDays(5), user, iaurt));

        // --- Reteta demo ---
        Recipe puiOrez = new Recipe("Pui cu orez si brocoli",
                "1. Fierbe orezul. 2. Gateste pieptul de pui la tigaie cu putin ulei. "
                        + "3. Aburind brocoli 5 minute. 4. Asambleaza si serveste.", user);
        puiOrez.getIngredients().add(new RecipeIngredient(200.0, UnitType.GRAMS, puiOrez, pieptPui));
        puiOrez.getIngredients().add(new RecipeIngredient(150.0, UnitType.GRAMS, puiOrez, orez));
        puiOrez.getIngredients().add(new RecipeIngredient(200.0, UnitType.GRAMS, puiOrez, brocoli));
        puiOrez.getIngredients().add(new RecipeIngredient(10.0, UnitType.MILLILITERS, puiOrez, ulei));
        recipeRepository.save(puiOrez);

        Recipe micDejun = new Recipe("Ovaz cu iaurt si banana",
                "1. Amesteca fulgii de ovaz cu iaurtul. 2. Adauga banana feliata. 3. Serveste.", user);
        micDejun.getIngredients().add(new RecipeIngredient(60.0, UnitType.GRAMS, micDejun, ovaz));
        micDejun.getIngredients().add(new RecipeIngredient(150.0, UnitType.GRAMS, micDejun, iaurt));
        micDejun.getIngredients().add(new RecipeIngredient(120.0, UnitType.GRAMS, micDejun, banana));
        recipeRepository.save(micDejun);

        // --- Jurnal alimentar (azi) ---
        mealLogRepository.save(new MealLog(60.0, UnitType.GRAMS, LocalDateTime.now().withHour(8).withMinute(0), user, ovaz));
        mealLogRepository.save(new MealLog(150.0, UnitType.GRAMS, LocalDateTime.now().withHour(8).withMinute(0), user, iaurt));
        mealLogRepository.save(new MealLog(200.0, UnitType.GRAMS, LocalDateTime.now().withHour(13).withMinute(30), user, pieptPui));

        System.out.println("==> [BiteWise] Date demo incarcate. Utilizator demo id=" + user.getId());
    }

    private Ingredient ing(String name, double cal, double prot, double carbs, double sugars, double fats, Category cat) {
        Ingredient i = new Ingredient(name, cal, prot, carbs, sugars, fats);
        i.setCategory(cat);
        return i;
    }
}
