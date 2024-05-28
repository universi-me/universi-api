package me.universi.group.repositories;

import me.universi.group.entities.GroupSettings.GroupEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupEnvironmentRepository extends JpaRepository<GroupEnvironment, UUID> {
    Optional<GroupEnvironment> findFirstById(UUID id);

    boolean existsByGroupSettingsId(UUID groupSettingsId);

    GroupEnvironment findFirstByGroupSettingsId(UUID groupSettingsId);
}
