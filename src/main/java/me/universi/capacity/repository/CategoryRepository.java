package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByName(String name);
    boolean existsByName(String name);
}
