package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Intrare in jurnalul alimentar: utilizatorul a consumat o cantitate dintr-un ingredient.
 */
@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amountConsumed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unit;

    @Column(nullable = false)
    private LocalDateTime consumedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public MealLog() {
    }

    public MealLog(Double amountConsumed, UnitType unit, LocalDateTime consumedAt, User user, Ingredient ingredient) {
        this.amountConsumed = amountConsumed;
        this.unit = unit;
        this.consumedAt = consumedAt;
        this.user = user;
        this.ingredient = ingredient;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmountConsumed() { return amountConsumed; }
    public void setAmountConsumed(Double amountConsumed) { this.amountConsumed = amountConsumed; }

    public UnitType getUnit() { return unit; }
    public void setUnit(UnitType unit) { this.unit = unit; }

    public LocalDateTime getConsumedAt() { return consumedAt; }
    public void setConsumedAt(LocalDateTime consumedAt) { this.consumedAt = consumedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}
