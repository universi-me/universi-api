package me.universi.capacity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Optional<Folder> findFirstByReference(String reference);
    Optional<Folder> findFirstByIdOrReference(UUID id, String reference);

    List<Folder> findByCategories(Category category);
}
