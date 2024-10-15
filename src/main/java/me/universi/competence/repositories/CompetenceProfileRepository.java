package me.universi.competence.repositories;

import me.universi.competence.entities.CompetenceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, UUID> {
}
