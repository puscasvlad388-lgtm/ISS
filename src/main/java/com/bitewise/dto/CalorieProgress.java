package com.bitewise.dto;

/** Progresul caloric pe o zi: consumat vs. tinta. */
public class CalorieProgress {
    private double consumedCalories;
    private double targetCalories;
    private double remaining;
    private double percentage;
    private NutritionSummary consumed;

    public CalorieProgress() {}

    public double getConsumedCalories() { return consumedCalories; }
    public void setConsumedCalories(double consumedCalories) { this.consumedCalories = consumedCalories; }
    public double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(double targetCalories) { this.targetCalories = targetCalories; }
    public double getRemaining() { return remaining; }
    public void setRemaining(double remaining) { this.remaining = remaining; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public NutritionSummary getConsumed() { return consumed; }
    public void setConsumed(NutritionSummary consumed) { this.consumed = consumed; }
}
