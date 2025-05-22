package me.universi.group.repositories;

import me.universi.group.entities.GroupSettings.GroupEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupEnvironmentRepository extends JpaRepository<GroupEnvironment, UUID> {
}
