package me.universi.group.repositories;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.entities.Subgroup;
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
public interface SubgroupRepository extends JpaRepository<Subgroup, UUID> {
    Optional<Subgroup> findFirstById(UUID id);

    boolean existsByGroupIdAndSubgroupId(UUID groupId, UUID profileId);

    Subgroup findFirstByGroupAndSubgroup(@NotNull Group group, @NotNull Group subgroup);
}
