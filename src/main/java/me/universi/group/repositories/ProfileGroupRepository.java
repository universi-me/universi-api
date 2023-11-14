package me.universi.group.repositories;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.profile.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileGroupRepository extends JpaRepository<ProfileGroup, UUID> {
    Optional<ProfileGroup> findFirstById(UUID id);

    boolean existsByGroupIdAndProfileId(UUID groupId, UUID profileId);

    ProfileGroup findFirstByGroupAndProfile(@NotNull Group group, @NotNull Profile profile);
}
