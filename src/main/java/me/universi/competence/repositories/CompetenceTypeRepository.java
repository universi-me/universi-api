package me.universi.competence.repositories;

import me.universi.competence.entities.CompetenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetenceTypeRepository extends JpaRepository<CompetenceType, UUID> {
    Optional<CompetenceType> findFirstByNameIgnoreCase(String name);
}
