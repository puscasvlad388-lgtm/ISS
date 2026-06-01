package com.bitewise.controller;

import com.bitewise.domain.Category;
import com.bitewise.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> all() { return categoryService.findAll(); }

    @GetMapping("/{id}")
    public Category one(@PathVariable Long id) { return categoryService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@Valid @RequestBody Category c) { return categoryService.create(c); }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @Valid @RequestBody Category c) { return categoryService.update(id, c); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
