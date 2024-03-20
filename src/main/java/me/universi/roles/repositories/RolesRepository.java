package me.universi.roles.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import me.universi.roles.entities.Roles;
import me.universi.roles.entities.RolesProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findFirstById(UUID id);
    Collection<Roles> findAllByGroup(Group group);
}
