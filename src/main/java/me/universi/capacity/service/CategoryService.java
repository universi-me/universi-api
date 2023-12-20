package me.universi.capacity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import me.universi.Sys;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.exceptions.CapacityException;
import me.universi.capacity.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public static CategoryService getInstance() {
        return Sys.context.getBean("categoryService", CategoryService.class);
    }

    public Category findById(UUID uuid) throws CapacityException {
        Category category = categoryRepository.findFirstById(uuid);
        if(category == null) {
            throw new CapacityException("Categoria não encontrada.");
        }
        return category;
    }

    public Category findById(String uuid) throws CapacityException {
        return findById(UUID.fromString(uuid));
    }

    public boolean saveOrUpdate(Category category) throws CapacityException {
        boolean titleExists = categoryRepository.existsByName(category.getName());
        if (titleExists) {
            throw new CapacityException("Categoria com título já existente.");
        }

        Category updatedCategory = categoryRepository.save(category);

        if (categoryRepository.findFirstById(updatedCategory.getId()) != null){
            return true;
        }

        return false;
    }

    public boolean delete(UUID id){
        categoryRepository.deleteById(id);
        return true;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
