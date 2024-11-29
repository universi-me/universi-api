package me.universi.image.repositories;

import java.util.Optional;
import java.util.UUID;
import me.universi.image.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findFirstById(UUID id);
}