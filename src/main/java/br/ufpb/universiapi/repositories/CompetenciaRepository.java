package br.ufpb.universiapi.repositories;

import br.ufpb.universiapi.entities.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
}
