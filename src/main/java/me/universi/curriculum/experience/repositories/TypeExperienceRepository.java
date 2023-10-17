package me.universi.curriculum.experience.repositories;

import me.universi.curriculum.experience.entities.TypeExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeExperienceRepository extends JpaRepository<TypeExperience, UUID>{
}

