package me.universi.group.repositories;

import me.universi.group.entities.GroupAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupAdminRepository extends JpaRepository<GroupAdmin, UUID> {
    Optional<GroupAdmin> findFirstById(UUID id);

    boolean existsByGroupIdAndProfileId(UUID groupId, UUID profileId);

    GroupAdmin findFirstByGroupIdAndProfileId(UUID groupId, UUID profileId);
}
