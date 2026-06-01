package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Ingredient cu valori nutritionale (exprimate per 100g/100ml).
 * Este clasificat de o Category.
 */
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double calories; // per 100g
    private double protein;
    private double carbs;
    private double sugars;
    private double fats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    public Ingredient() {
    }

    public Ingredient(String name, double calories, double protein, double carbs, double sugars, double fats) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
        this.fats = fats;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getSugars() { return sugars; }
    public void setSugars(double sugars) { this.sugars = sugars; }

    public double getFats() { return fats; }
    public void setFats(double fats) { this.fats = fats; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
