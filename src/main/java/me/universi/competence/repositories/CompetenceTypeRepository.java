package me.universi.competence.repositories;

import me.universi.competence.entities.CompetenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetenceTypeRepository extends JpaRepository<CompetenceType, Long> {
    Optional<CompetenceType> findFirstById(Long id);
    Optional<CompetenceType> findFirstByName(String name);

    Optional<CompetenceType> findById(Long id);
}
