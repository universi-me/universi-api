package me.universi.roles.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.roles.entities.Roles;
import me.universi.roles.entities.RolesProfile;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesProfileRepository extends JpaRepository<RolesProfile, UUID> {
    Optional<RolesProfile> findFirstById(UUID uuid);
    Optional<RolesProfile> findFirstByRolesAndProfile(Roles roles, Profile feature);

    Optional<RolesProfile> findFirstByProfileAndGroup(Profile profile, Group group);

    Collection<RolesProfile> findAllByGroup(Group group);

    Optional<RolesProfile> findFirstByRolesAndProfileAndGroup(Roles roles, Profile profile, Group group);

    Collection<RolesProfile> findAllByProfile(Profile profile);
}
