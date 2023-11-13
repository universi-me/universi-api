package me.universi.user.repositories;

import me.universi.group.entities.Group;
import me.universi.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByEmailAndOrganization(String email, Group organization);
    Optional<User> findFirstByName(String name);
    Optional<User> findFirstByNameAndOrganization(String name, Group organization);
    Optional<User> findFirstById(UUID id);
    Optional<User> findFirstByRecoveryPasswordToken(String token);
}
