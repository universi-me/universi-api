package me.universi.capacity.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Folder findFirstById(UUID id);
    List<Folder> findByName(String name);

    Collection<Folder> findByCategories(Category category);
}