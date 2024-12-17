package me.universi.experience.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.experience.entities.TypeExperience;

import java.util.UUID;

@Repository
public interface TypeExperienceRepository extends JpaRepository<TypeExperience, UUID>{
    Optional<TypeExperience> findFirstById(UUID id);
}

