package com.bitewise.service;

import com.bitewise.domain.Category;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria cu id " + id + " nu exista"));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category data) {
        Category existing = findById(id);
        existing.setName(data.getName());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        Category existing = findById(id);
        categoryRepository.delete(existing);
    }
}
