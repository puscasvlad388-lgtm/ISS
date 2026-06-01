package com.bitewise.repository;

import com.bitewise.domain.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {
    List<PantryItem> findByUserId(Long userId);
    List<PantryItem> findByUserIdAndExpirationDateBefore(Long userId, LocalDate date);
}
