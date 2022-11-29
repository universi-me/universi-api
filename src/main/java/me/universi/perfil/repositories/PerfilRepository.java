package me.universi.perfil.repositories;

import me.universi.perfil.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findFirstById(Long id);
}
