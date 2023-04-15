package me.universi.grupo.repositories;

import me.universi.grupo.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Group, Long> {
    Optional<Group> findFirstById(Long id);
    Optional<Group> findFirstByNickname(String nickname);
    Optional<Group> findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname);
    List<Group> findByGrupoPublico(boolean grupoPublico);
    @Query(value = "SELECT ID_GRUPO FROM GRUPO_GRUPO WHERE ID_SUBGRUPO = :IDGrupo LIMIT 1", nativeQuery = true)
    Optional<Long> findGrupoIdPaiDoGrupoId(@Param("IDGrupo") Long id);
    Collection<Group> findTop5ByNomeContainingIgnoreCase(String nome);
}
