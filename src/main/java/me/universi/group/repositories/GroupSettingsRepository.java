package me.universi.group.repositories;

import me.universi.group.entities.GroupSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupSettingsRepository extends JpaRepository<GroupSettings, UUID> {
    Optional<GroupSettings> findFirstById(UUID id);
}
