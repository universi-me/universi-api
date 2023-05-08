package me.universi.competence.repositories;

import me.universi.competence.entities.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    Optional<Competence> findFirstById(Long id);
}
