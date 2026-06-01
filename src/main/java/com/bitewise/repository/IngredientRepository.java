package com.bitewise.repository;

import com.bitewise.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCase(String name);
    List<Ingredient> findByCategoryId(Long categoryId);
}
