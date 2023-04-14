package me.universi.competencia.repositories;

import me.universi.competencia.entities.CompetenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetenciaTipoRepository extends JpaRepository<CompetenceType, Long> {
    Optional<CompetenceType> findFirstById(Long id);
    Optional<CompetenceType> findFirstByNome(String nome);
}
