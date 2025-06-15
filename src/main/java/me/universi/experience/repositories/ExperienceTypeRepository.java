package me.universi.experience.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.experience.entities.ExperienceType;

import java.util.UUID;

@Repository
public interface ExperienceTypeRepository extends JpaRepository<ExperienceType, UUID>{
    Optional<ExperienceType> findFirstByNameIgnoreCase( String name );
    Optional<ExperienceType> findFirstByIdOrNameIgnoreCase( UUID id, String name );
}
