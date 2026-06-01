package com.bitewise.repository;

import com.bitewise.domain.WeightTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeightTrackerRepository extends JpaRepository<WeightTracker, Long> {
    List<WeightTracker> findByUserIdOrderByDateAsc(Long userId);
}
