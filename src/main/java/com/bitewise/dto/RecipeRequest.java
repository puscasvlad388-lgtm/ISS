package com.bitewise.dto;

import com.bitewise.domain.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** Payload pentru crearea/actualizarea unei retete cu ingredientele ei. */
public class RecipeRequest {
    @NotBlank
    private String title;
    private String instructions;
    private List<Line> ingredients = new ArrayList<>();

    public static class Line {
        @NotNull private Long ingredientId;
        @NotNull private Double quantity;
        @NotNull private UnitType unit;

        public Long getIngredientId() { return ingredientId; }
        public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }
        public UnitType getUnit() { return unit; }
        public void setUnit(UnitType unit) { this.unit = unit; }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public List<Line> getIngredients() { return ingredients; }
    public void setIngredients(List<Line> ingredients) { this.ingredients = ingredients; }
}
