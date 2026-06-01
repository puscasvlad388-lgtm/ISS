package com.bitewise.dto;

/** Sumar nutritional agregat (calorii + macro). */
public class NutritionSummary {
    private double calories;
    private double protein;
    private double carbs;
    private double sugars;
    private double fats;

    public NutritionSummary() {}

    public NutritionSummary(double calories, double protein, double carbs, double sugars, double fats) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugars = sugars;
        this.fats = fats;
    }

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

    public void add(NutritionSummary o) {
        this.calories += o.calories;
        this.protein += o.protein;
        this.carbs += o.carbs;
        this.sugars += o.sugars;
        this.fats += o.fats;
    }
}
