package me.universi.roles.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.roles.entities.Roles;
import me.universi.roles.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findFirstById(UUID id);
    Collection<Roles> findAllByGroup(Group group);
    Optional<Roles> findFirstByNameIgnoreCaseAndGroupId( String name, UUID groupId );

    Roles findFirstByGroupIdAndRoleType(UUID groupId, RoleType roleType);
}
