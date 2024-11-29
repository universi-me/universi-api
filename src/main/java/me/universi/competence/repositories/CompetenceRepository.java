package me.universi.competence.repositories;

import me.universi.competence.entities.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, UUID> {
    Optional<Competence> findFirstById(UUID id);
}
