package me.universi.group.repositories;


import me.universi.group.entities.GroupSettings.GroupEmailFilter;
import me.universi.group.entities.GroupSettings.GroupTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupThemeRepository extends JpaRepository<GroupTheme, UUID> {
    Optional<GroupTheme> findFirstById(UUID id);

    boolean existsByGroupSettingsId(UUID id);

    GroupTheme findFirstByGroupSettingsId(UUID id);
}
