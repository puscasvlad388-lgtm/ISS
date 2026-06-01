package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilizatorul aplicatiei BiteWise.
 * Relatii (conform diagramei):
 *  - inregistreaza  -> WeightTracker (1..*)
 *  - seteaza        -> DietaryGoal  (0..1)
 *  - consuma        -> MealLog      (1..*)
 *  - detine         -> PantryItem   (1..*)
 *  - creeaza        -> Recipe       (1..*)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private Double weight;   // kg
    private Double height;   // cm
    private Integer age;
    private String gender;   // "male" / "female" / etc.

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeightTracker> weightHistory = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private DietaryGoal dietaryGoal;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealLog> mealLogs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PantryItem> pantryItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, Double weight, Double height, Integer age, String gender) {
        this.name = name;
        this.email = email;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<WeightTracker> getWeightHistory() { return weightHistory; }
    public void setWeightHistory(List<WeightTracker> weightHistory) { this.weightHistory = weightHistory; }

    public DietaryGoal getDietaryGoal() { return dietaryGoal; }
    public void setDietaryGoal(DietaryGoal dietaryGoal) { this.dietaryGoal = dietaryGoal; }

    public List<MealLog> getMealLogs() { return mealLogs; }
    public void setMealLogs(List<MealLog> mealLogs) { this.mealLogs = mealLogs; }

    public List<PantryItem> getPantryItems() { return pantryItems; }
    public void setPantryItems(List<PantryItem> pantryItems) { this.pantryItems = pantryItems; }

    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
}
