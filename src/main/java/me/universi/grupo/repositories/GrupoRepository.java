package me.universi.grupo.repositories;

import me.universi.grupo.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findFirstById(Long id);
    Optional<Grupo> findFirstByNickname(String nickname);
    Optional<Grupo> findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname);
}
