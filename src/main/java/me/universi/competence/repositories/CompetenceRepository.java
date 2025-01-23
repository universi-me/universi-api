package me.universi.competence.repositories;

import me.universi.competence.entities.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, UUID> {
    List<Competence> findByProfileId( UUID profileId );
    List<Competence> findByProfileIdAndCompetenceTypeId( UUID profileId, UUID competenceTypeId );
}
