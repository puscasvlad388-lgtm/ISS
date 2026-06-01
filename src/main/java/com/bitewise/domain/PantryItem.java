package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Aliment aflat in camara utilizatorului (stoc), cu data de expirare.
 * Stocheaza un Ingredient.
 */
@Entity
@Table(name = "pantry_items")
public class PantryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double quantity;

    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public PantryItem() {
    }

    public PantryItem(Double quantity, LocalDate expirationDate, User user, Ingredient ingredient) {
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.user = user;
        this.ingredient = ingredient;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}
