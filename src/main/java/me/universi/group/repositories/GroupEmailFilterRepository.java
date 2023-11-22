package me.universi.group.repositories;


import me.universi.group.entities.GroupSettings.GroupEmailFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupEmailFilterRepository extends JpaRepository<GroupEmailFilter, UUID> {
    Optional<GroupEmailFilter> findFirstById(UUID id);

    boolean existsByGroupSettingsIdAndId(UUID groupSettingsId, UUID id);

    GroupEmailFilter findFirstByGroupSettingsIdAndId(UUID groupSettingsId, UUID id);
}
