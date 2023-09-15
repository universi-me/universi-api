package me.universi.curriculum.component.repositories;


import me.universi.curriculum.component.entities.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComponentRespository extends JpaRepository<Component, UUID> {

    Optional<Component> findFirstById(UUID id);

    List<Component> findByProfileId(UUID profileId);
}
