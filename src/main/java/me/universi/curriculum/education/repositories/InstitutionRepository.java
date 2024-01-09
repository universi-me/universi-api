package me.universi.curriculum.education.repositories;

import java.util.Optional;
import me.universi.curriculum.education.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {
    Optional<Institution> findFirstById(UUID id);
}

