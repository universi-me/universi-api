package me.universi.group.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.group.entities.GroupType;
import me.universi.group.enums.GroupTypeKind;

public interface GroupTypeRepository extends JpaRepository<GroupType, UUID> {
    Optional<GroupType> findFirstByLabelIgnoreCase( String label );
    Optional<GroupType> findFirstByIdOrLabelIgnoreCase( UUID id, String label );
    GroupType findFirstByKind( GroupTypeKind kind );
}
