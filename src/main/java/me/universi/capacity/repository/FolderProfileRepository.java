package me.universi.capacity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.universi.capacity.entidades.FolderProfile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FolderProfileRepository extends JpaRepository<FolderProfile, UUID> {
    FolderProfile findFirstById(UUID id);

    List<FolderProfile> findByAssignedToId(UUID assignedToId);

    List<FolderProfile> findByFolderIdAndAssignedById(UUID folderId, UUID assignedById);

    List<FolderProfile> findByAssignedById(UUID assignedById);
    List<FolderProfile> findByAssignedByIdAndAssignedToId( UUID assignedById, UUID assignedToId );

    Optional<FolderProfile> findByFolderIdAndAssignedToIdAndAssignedById( UUID folderId, UUID assignedToId, UUID assignedById );
}
