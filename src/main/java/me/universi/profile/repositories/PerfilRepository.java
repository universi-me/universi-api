package me.universi.profile.repositories;

import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findFirstById(UUID id);

    @Query( "SELECT p FROM Profile p INNER JOIN User u ON p.user.id = u.id WHERE p.id = ?1 OR u.name = ?2" )
    Optional<Profile> findByIdOrUsername( UUID id, String username );

    Collection<Profile> findTop5ByFirstnameContainingIgnoreCase(String firstName);

    boolean existsByIdAndDeletedFalse(UUID id);
}
