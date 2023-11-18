package me.universi.group.repositories;

import jakarta.transaction.Transactional;
import me.universi.group.entities.Group;
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
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findFirstById(UUID id);
    Optional<Group> findFirstByNickname(String nickname);
    Optional<Group> findFirstByRootGroupAndNicknameIgnoreCase(boolean rootGroup, String nickname);
    List<Group> findByPublicGroup(boolean grupoPublico);
    @Query(value = "SELECT group_id FROM subgroup WHERE subgroup_id = :GroupId LIMIT 1", nativeQuery = true)
    Optional<Object> findParentGroupId(@Param("GroupId") UUID id);

    Collection<Group> findTop5ByNameContainingIgnoreCase(String nome);

    Optional<Group> findFirstByIdAndAdminId(UUID groupId, UUID profileId);

    Boolean existsByIdAndParticipantsId(UUID groupId, UUID profileId);

    boolean existsByAdministratorsIdAndId(UUID id, UUID id1);
}
