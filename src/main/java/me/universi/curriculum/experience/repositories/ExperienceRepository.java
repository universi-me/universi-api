package me.universi.curriculum.experience.repositories;

import java.util.Optional;
import me.universi.curriculum.experience.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    Optional<Experience> findFirstById(UUID id);
}

