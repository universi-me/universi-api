package me.universi.capacity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.capacity.entidades.FolderFavorite;

public interface FolderFavoriteRepository extends JpaRepository<FolderFavorite, UUID> {
    FolderFavorite findFirstById(UUID id);

    List<FolderFavorite> findByProfileId(UUID profileId);

    boolean existsByFolderIdAndProfileId(UUID folderId, UUID profileId);
    Optional<FolderFavorite> findByFolderIdAndProfileId(UUID folderId, UUID profileId);
}
