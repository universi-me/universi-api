package me.universi.projeto.repositories;

import br.ufpb.universiapi.entities.Usuario;
import me.universi.projeto.entities.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
}
