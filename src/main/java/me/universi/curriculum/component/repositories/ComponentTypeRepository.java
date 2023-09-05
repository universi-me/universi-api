package me.universi.curriculum.component.repositories;


import me.universi.curriculum.component.entities.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentType, UUID> {

    Optional <ComponentType> findFirstById(UUID id);
    ComponentType findFirstByName(String name);

}
