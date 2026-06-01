package com.bitewise.repository;

import com.bitewise.domain.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserId(Long userId);
    List<MealLog> findByUserIdAndConsumedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
