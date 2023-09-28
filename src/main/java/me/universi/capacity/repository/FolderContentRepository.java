package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderContent;

public interface FolderContentRepository extends JpaRepository<FolderContent, UUID> {
    FolderContent findFirstById(UUID id);
    boolean existsByFolder(Folder folder);
    List<FolderContent> findByContent(Content content);
    List<FolderContent> findByFolder(Folder folder);
    FolderContent findFirstByContentAndFolder(Content content, Folder folder);

    @Query(nativeQuery = false, value = "SELECT fc FROM FolderContent fc WHERE fc.folder.id = :folder AND fc.previousContent IS NULL")
    FolderContent findFirstContentFolderInFolder(@Param("folder") UUID folder);

    @Query(nativeQuery = false, value = "SELECT fc FROM FolderContent fc WHERE fc.previousContent = :nextOf")
    FolderContent findNextFolderContent(@Param("nextOf") FolderContent nextOf);
}
