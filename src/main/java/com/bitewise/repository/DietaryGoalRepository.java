package com.bitewise.repository;

import com.bitewise.domain.DietaryGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DietaryGoalRepository extends JpaRepository<DietaryGoal, Long> {
    Optional<DietaryGoal> findByUserId(Long userId);
}
