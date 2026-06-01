package com.bitewise.dto;

import com.bitewise.domain.UnitType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Payload pentru inregistrarea unei mese in jurnal. */
public class MealLogRequest {
    @NotNull private Long ingredientId;
    @NotNull private Double amountConsumed;
    @NotNull private UnitType unit;
    private LocalDateTime consumedAt;

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public Double getAmountConsumed() { return amountConsumed; }
    public void setAmountConsumed(Double amountConsumed) { this.amountConsumed = amountConsumed; }
    public UnitType getUnit() { return unit; }
    public void setUnit(UnitType unit) { this.unit = unit; }
    public LocalDateTime getConsumedAt() { return consumedAt; }
    public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }
}
