package me.universi.grupo.repositories;

import me.universi.grupo.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findFirstById(Long id);
    Optional<Grupo> findFirstByNickname(String nickname);
    Optional<Grupo> findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname);
    @Query(value = "SELECT * FROM GRUPO_GRUPO WHERE id_subgrupo = ?1 LIMIT 1", nativeQuery = true)
    Optional<Grupo> findGrupoPaiDoGrupo(Long id);
}
