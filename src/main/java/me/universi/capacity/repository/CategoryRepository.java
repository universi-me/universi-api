package me.universi.capacity.repository;

import java.util.Optional;
import java.util.UUID;

import me.universi.capacity.entidades.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findFirstByNameIgnoreCase( String name );
    Optional<Category> findFirstByIdOrNameIgnoreCase( UUID id, String name );

    boolean existsByName(String name);
}
