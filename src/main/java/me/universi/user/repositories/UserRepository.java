package me.universi.user.repositories;

import java.util.List;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByEmailAndOrganizationId(String email, UUID organization);
    Optional<User> findFirstByName(String name);
    Optional<User> findFirstByNameAndOrganizationId(String name, UUID organization);
    Optional<User> findFirstById(UUID id);
    Optional<User> findFirstByRecoveryPasswordToken(String token);
    List<User> findAllByAuthority(Authority authority);
    List<User> findAllByAuthorityAndOrganizationId(Authority authority, UUID organization);
    List<User> findAllByOrganizationId(UUID organization);

    @Query( "SELECT u FROM User u WHERE u.email = :nameOrEmail OR u.name = :nameOrEmail" )
    Optional<User> findFirstByEmailOrName( @Param("nameOrEmail") String nameOrEmail );

    @Query( "SELECT u FROM User u WHERE ( u.organization.id = :organizationId ) AND ( u.email = :nameOrEmail OR u.name = :nameOrEmail )" )
    Optional<User> findFirstByEmailOrNameAndOrganizationId( @Param("nameOrEmail") String nameOrEmail, @Param("organizationId") UUID organizationId );

    boolean existsByIdAndDeletedFalse(UUID id);
}
