package me.universi.capacity.repository;

import java.util.List;
import java.util.UUID;
import me.universi.capacity.entidades.FolderProfile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FolderProfileRepository extends JpaRepository<FolderProfile, UUID> {
    FolderProfile findFirstById(UUID id);

    List<FolderProfile> findByProfileIdAndAssigned(UUID profileId, boolean assigned);

    boolean existsByFolderIdAndProfileId(UUID folderId, UUID profileId);
    FolderProfile findFirstByFolderIdAndProfileId(UUID profileId, UUID folderId);

    List<FolderProfile> findByFolderIdAndAssigned(UUID folderId, boolean assigned);

    List<FolderProfile> findByAuthorId(UUID authorId);
}
