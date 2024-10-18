package me.universi.capacity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.universi.capacity.entidades.FolderProfile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FolderProfileRepository extends JpaRepository<FolderProfile, UUID> {
    FolderProfile findFirstById(UUID id);

    List<FolderProfile> findByProfileIdAndAssigned(UUID profileId, boolean assigned);

    List<FolderProfile> findByFolderIdAndAssignedAndAuthorId(UUID folderId, boolean assigned, UUID authorId);

    List<FolderProfile> findByAuthorId(UUID authorId);

    Optional<FolderProfile> findByFolderIdAndProfileIdAndAuthorId( UUID folderId, UUID profileId, UUID authorId );
}
