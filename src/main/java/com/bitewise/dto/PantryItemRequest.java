package com.bitewise.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Payload pentru adaugarea/actualizarea unui aliment in camara. */
public class PantryItemRequest {
    @NotNull private Long ingredientId;
    @NotNull private Double quantity;
    private LocalDate expirationDate;

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}
