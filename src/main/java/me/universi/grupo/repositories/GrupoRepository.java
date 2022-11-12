package me.universi.grupo.repositories;

import me.universi.grupo.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNickname(String nickname);
    Optional<Grupo> findByNicknameAndGrupoRoot(String nickname, boolean grupoRoot);
}
