package me.universi.group.repositories;

import jakarta.validation.constraints.NotNull;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileGroupRepository extends JpaRepository<ProfileGroup, UUID> {
    Optional<ProfileGroup> findFirstById(UUID id);

    boolean existsByGroupIdAndProfileId(UUID groupId, UUID profileId);

    Optional<ProfileGroup> findFirstByGroupAndProfile(@NotNull Group group, @NotNull Profile profile);
    Collection<ProfileGroup> findAllByProfile(@NotNull Profile profile);
    Collection<ProfileGroup> findAllByRoleId( @NotNull UUID roleId );
}
