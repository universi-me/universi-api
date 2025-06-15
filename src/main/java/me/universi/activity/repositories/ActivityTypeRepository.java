package me.universi.activity.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.activity.entities.ActivityType;

@Repository
public interface ActivityTypeRepository extends JpaRepository<ActivityType, UUID> {
    Optional<ActivityType> findFirstByNameIgnoreCase( String name );
    Optional<ActivityType> findFirstByIdOrNameIgnoreCase( UUID id, String name );
}
