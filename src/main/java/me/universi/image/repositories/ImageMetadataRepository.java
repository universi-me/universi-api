package me.universi.image.repositories;

import java.util.Optional;
import java.util.UUID;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, UUID> {
    Optional<ImageMetadata> findFirstByFilenameAndImageStore( String filename, ImageStoreLocation storeType );
    Optional<ImageMetadata> findFirstByFilename( String filename );
}
