package me.universi.recomendacao.repositories;

import me.universi.recomendacao.entities.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecomendacaoRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findFirstById(Long id);
}
