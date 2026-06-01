package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Obiectivul nutritional setat de utilizator (relatie 0..1).
 */
@Entity
@Table(name = "dietary_goals")
public class DietaryGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double targetCalories;
    private Double targetProtein;
    private Double targetCarbs;
    private Double targetFats;
    private Double targetSugar;

    private String goalType; // "lose" / "maintain" / "gain"

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public DietaryGoal() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }

    public Double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(Double targetProtein) { this.targetProtein = targetProtein; }

    public Double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(Double targetCarbs) { this.targetCarbs = targetCarbs; }

    public Double getTargetFats() { return targetFats; }
    public void setTargetFats(Double targetFats) { this.targetFats = targetFats; }

    public Double getTargetSugar() { return targetSugar; }
    public void setTargetSugar(Double targetSugar) { this.targetSugar = targetSugar; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
