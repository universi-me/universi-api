package me.universi.profile.repositories;

import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findFirstById(UUID id);
    Collection<Profile> findTop5ByFirstnameContainingIgnoreCase(String firstName);
}
