package me.universi.group.repositories;


import me.universi.group.entities.GroupSettings.GroupFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupFeaturesRepository extends JpaRepository<GroupFeatures, UUID> {
    Optional<GroupFeatures> findFirstById(UUID id);

    boolean existsByGroupSettingsIdAndId(UUID groupSettingsId, UUID id);

    GroupFeatures findFirstByGroupSettingsIdAndId(UUID groupSettingsId, UUID id);
}
