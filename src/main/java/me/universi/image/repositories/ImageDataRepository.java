package me.universi.image.repositories;

import java.util.Optional;
import java.util.UUID;

import me.universi.image.entities.ImageData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, UUID> {
    Optional<ImageData> findByMetadataFilename( String filename );
}
