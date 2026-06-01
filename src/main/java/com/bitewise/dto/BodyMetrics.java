package com.bitewise.dto;

/** Rezultatul calculelor BMI / BMR / TDEE pentru un utilizator. */
public class BodyMetrics {
    private double bmi;
    private String bmiCategory;
    private double bmr;
    private double tdee;

    public BodyMetrics() {}

    public BodyMetrics(double bmi, String bmiCategory, double bmr, double tdee) {
        this.bmi = bmi;
        this.bmiCategory = bmiCategory;
        this.bmr = bmr;
        this.tdee = tdee;
    }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }
    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }
    public double getTdee() { return tdee; }
    public void setTdee(double tdee) { this.tdee = tdee; }
}
