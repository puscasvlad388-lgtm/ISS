package com.bitewise.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Inregistrare de greutate la o anumita data (istoricul greutatii).
 */
@Entity
@Table(name = "weight_tracker")
public class WeightTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double recordedWeight; // kg

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public WeightTracker() {
    }

    public WeightTracker(Double recordedWeight, LocalDate date, User user) {
        this.recordedWeight = recordedWeight;
        this.date = date;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getRecordedWeight() { return recordedWeight; }
    public void setRecordedWeight(Double recordedWeight) { this.recordedWeight = recordedWeight; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
