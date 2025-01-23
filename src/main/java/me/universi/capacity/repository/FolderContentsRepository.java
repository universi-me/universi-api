package me.universi.capacity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import me.universi.capacity.entidades.FolderContents;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderContentsRepository extends JpaRepository<FolderContents, UUID> {
    List<FolderContents> findByFolderId( UUID folderId );
    Optional<FolderContents> findByFolderIdAndContentId( UUID folderId, UUID contentId );
    List<FolderContents> findByFolderIdOrderByOrderNumAsc( UUID folderId );
}
