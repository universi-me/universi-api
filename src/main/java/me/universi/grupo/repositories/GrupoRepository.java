package me.universi.grupo.repositories;

import me.universi.grupo.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findFirstById(Long id);
    Optional<Grupo> findFirstByNickname(String nickname);
    Optional<Grupo> findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname);
    List<Grupo> findByGrupoPublico(boolean grupoPublico);
    @Query(value = "SELECT ID_GRUPO FROM GRUPO_GRUPO WHERE ID_SUBGRUPO = :IDGrupo LIMIT 1", nativeQuery = true)
    Optional<Long> findGrupoIdPaiDoGrupoId(@Param("IDGrupo") Long id);
}
