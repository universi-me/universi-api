package me.universi.competencia.repositories;

import me.universi.competencia.entities.CompetenciaTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetenciaTipoRepository extends JpaRepository<CompetenciaTipo, Long> {
    Optional<CompetenciaTipo> findFirstById(Long id);
    Optional<CompetenciaTipo> findFirstByNome(String nome);
}
