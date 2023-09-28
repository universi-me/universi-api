package me.universi.capacity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.universi.capacity.entidades.FolderContent;

public interface FolderContentRepository extends JpaRepository<FolderContent, UUID> {
    FolderContent findFirstById(UUID id);
    boolean existsByFolder(UUID folder);

    @Query(nativeQuery = false, value = "SELECT * FROM FolderContent WHERE FolderContent.folder.id = folder_id AND FolderContent.previousContent IS NULL")
    FolderContent findFirstContentFolderInFolder(@Param("folder_id") UUID folder_id);

    @Query(nativeQuery = false, value = "SELECT * FROM FolderContent WHERE FolderContent.previous.id = folder_content_id")
    FolderContent findNextFolderContent(@Param("folder_content_id") UUID folder_content_id);
}
