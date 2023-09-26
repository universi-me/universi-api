package me.universi.curriculum.profileExperience.repositories;

import me.universi.curriculum.profileExperience.entities.TypeExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeExperienceRepository extends JpaRepository<TypeExperience, UUID>{
}
