package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    Content findFirstById(UUID id);
    boolean existsByTitle(String title);
    boolean existsByUrl(String url);
    List<Content> findByCategories(Category category);
}