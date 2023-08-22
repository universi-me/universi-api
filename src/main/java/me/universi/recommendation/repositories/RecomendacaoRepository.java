package me.universi.recommendation.repositories;

import me.universi.recommendation.entities.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecomendacaoRepository extends JpaRepository<Recommendation, UUID> {
    Optional<Recommendation> findFirstById(UUID id);
}
