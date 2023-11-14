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

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO profile_group (group_id, profile_id) VALUES (:GroupId, :ProfileId)", nativeQuery = true)
    void addParticipant(@Param("GroupId") UUID groupId, @Param("ProfileId") UUID profileId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM profile_group WHERE group_id=:GroupId AND profile_id=:ProfileId", nativeQuery = true)
    void removeParticipant(@Param("GroupId") UUID groupId, @Param("ProfileId") UUID profileId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO subgroup (group_id, subgroup_id) VALUES (:GroupId, :GroupId2)", nativeQuery = true)
    void addGroup(@Param("GroupId") UUID groupId, @Param("GroupId2") UUID groupId2);

    Collection<Group> findTop5ByNameContainingIgnoreCase(String nome);

    Optional<Group> findFirstByIdAndAdminId(UUID groupId, UUID profileId);

    Boolean existsByIdAndAdminId(UUID groupId, UUID adminId);

    Boolean existsByIdAndParticipantsId(UUID groupId, UUID profileId);

    Boolean existsByIdAndSubGroupsId(UUID groupId, UUID profileId);

}
