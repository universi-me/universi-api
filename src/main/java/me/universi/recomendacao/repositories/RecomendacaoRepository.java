package me.universi.recomendacao.repositories;

import me.universi.recomendacao.entities.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {
}
