package me.universi.perfil.repositories;

import me.universi.perfil.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findFirstById(Long id);
    Collection<Profile> findTop5ByFirstnameContainingIgnoreCase(String nome);
}
