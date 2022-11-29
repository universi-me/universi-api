package me.universi.competencia.repositories;

import me.universi.competencia.entities.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
    Optional<Competencia> findFirstById(Long id);
}
