package me.universi.competence.repositories;

import me.universi.competence.entities.CompetenceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, UUID> {
    public List<CompetenceProfile> findByProfileId( UUID profileId );

    @Query( "SELECT cp FROM CompetenceProfile cp WHERE cp.profile.id = :profileId AND cp.competence.competenceType.id = :competenceTypeId" )
    public Optional<CompetenceProfile> findByProfileIdAndCompetenceTypeId(
        @Param("profileId") UUID profileId,
        @Param("competenceTypeId") UUID competenceTypeId
    );
}
