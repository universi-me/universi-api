package me.universi.role.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import me.universi.group.entities.Group;
import me.universi.role.entities.Role;
import me.universi.role.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findFirstById(UUID id);
    Collection<Role> findAllByGroup(Group group);
    Optional<Role> findFirstByNameIgnoreCaseAndGroupId( String name, UUID groupId );

    Role findFirstByGroupIdAndRoleType(UUID groupId, RoleType roleType);
}
