package me.universi.group.repositories;


import me.universi.group.entities.GroupSettings.GroupTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupThemeRepository extends JpaRepository<GroupTheme, UUID> {
}
